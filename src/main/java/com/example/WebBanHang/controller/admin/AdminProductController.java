package com.example.WebBanHang.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.dto.ProductDto;
import com.example.WebBanHang.model.Product;
import com.example.WebBanHang.model.ProductVariant;
import com.example.WebBanHang.repository.*;
import com.example.WebBanHang.service.ProductService;
import com.example.WebBanHang.service.ProductVariantService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired private ProductService productService;
    @Autowired private ProductVariantService variantService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private SportRepository sportRepository;
    @Autowired private ColorRepository colorRepository;
    @Autowired private SizeRepository sizeRepository;
    @Autowired private ProductImageRepository productImageRepository;

    // ==================== HELPER MAPS ====================
    private Map<Integer, String> buildCategoryMap() {
        Map<Integer, String> map = new HashMap<>();
        categoryRepository.findAll().forEach(c -> map.put(c.getId(), c.getName()));
        return map;
    }

    private Map<Integer, String> buildBrandMap() {
        Map<Integer, String> map = new HashMap<>();
        brandRepository.findAll().forEach(b -> map.put(b.getId(), b.getName()));
        return map;
    }

    private Map<Integer, String> buildSportMap() {
        Map<Integer, String> map = new HashMap<>();
        sportRepository.findAll().forEach(s -> map.put(s.getId(), s.getName()));
        return map;
    }

    private Map<Integer, String> buildColorMap() {
        Map<Integer, String> map = new HashMap<>();
        colorRepository.findAll().forEach(c -> map.put(c.getId(), c.getName()));
        return map;
    }

    private Map<Integer, String> buildColorCodeMap() {
        Map<Integer, String> map = new HashMap<>();
        colorRepository.findAll().forEach(c -> map.put(c.getId(), c.getCode()));
        return map;
    }

    private Map<Integer, String> buildSizeMap() {
        Map<Integer, String> map = new HashMap<>();
        sizeRepository.findAll().forEach(s -> map.put(s.getId(), s.getName()));
        return map;
    }

    private String uploadFile(MultipartFile file) {
        try {
            if (file.isEmpty()) return null;
            java.io.File dir = new java.io.File("uploads/");
            if (!dir.exists()) dir.mkdirs();
            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = java.util.UUID.randomUUID().toString() + extension;
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads/", newFilename);
            java.nio.file.Files.write(filePath, file.getBytes());
            return "/uploads/" + newFilename;
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== SSR ROUTES ====================
    @GetMapping({"", "/"})
    public String productList(Model model) {
        model.addAttribute("activePage", "products");
        model.addAttribute("pageTitle", "Sản phẩm");
        
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        model.addAttribute("products", products);
        model.addAttribute("categoryMap", buildCategoryMap());
        model.addAttribute("contentFragment", "admin/products :: content");
        return "admin/layout";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("activePage", "products");
        model.addAttribute("pageTitle", "Chi tiết sản phẩm");
        
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return "redirect:/admin/products";

        model.addAttribute("product", product);
        model.addAttribute("categoryMap", buildCategoryMap());
        model.addAttribute("brandMap", buildBrandMap());
        model.addAttribute("sportMap", buildSportMap());
        model.addAttribute("colorMap", buildColorMap());
        model.addAttribute("colorCodeMap", buildColorCodeMap());
        model.addAttribute("sizeMap", buildSizeMap());

        List<ProductVariant> variants = variantService.getAllProductVariants(id);
        model.addAttribute("variants", variants != null ? variants : List.of());

        List<com.example.WebBanHang.model.ProductImage> images = productImageRepository.findAllByProductId(id);
        model.addAttribute("images", images != null ? images : List.of());

        // For modals
        model.addAttribute("sizes", sizeRepository.findAllByOrderByOrderAsc());
        model.addAttribute("colors", colorRepository.findAll());

        model.addAttribute("contentFragment", "admin/product-detail :: content");
        return "admin/layout";
    }

    @GetMapping("/add")
    public String productAddForm(Model model) {
        model.addAttribute("activePage", "products");
        model.addAttribute("pageTitle", "Thêm sản phẩm");
        
        model.addAttribute("product", null);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("sports", sportRepository.findAll());
        model.addAttribute("contentFragment", "admin/product-form :: content");
        return "admin/layout";
    }

    @GetMapping("/{id}/edit")
    public String productEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("activePage", "products");
        model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
        
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return "redirect:/admin/products";

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("sports", sportRepository.findAll());
        model.addAttribute("contentFragment", "admin/product-form :: content");
        return "admin/layout";
    }

    @PostMapping("/add")
    public String productAdd(
            @RequestParam String name,
            @RequestParam Integer categoryId,
            @RequestParam Integer brandId,
            @RequestParam Integer sportId,
            @RequestParam(required = false) String gender,
            @RequestParam Long basePrice,
            @RequestParam(required = false) Long salePrice,
            @RequestParam(required = false) String saleStart,
            @RequestParam(required = false) String saleEnd,
            @RequestParam(required = false) String thumbnailUrl,
            @RequestParam(required = false) MultipartFile thumbnailFile,
            @RequestParam(required = false) String description,
            RedirectAttributes ra) {
        try {
            String finalThumbnailUrl = thumbnailUrl;
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String uploadedUrl = uploadFile(thumbnailFile);
                if (uploadedUrl != null) finalThumbnailUrl = uploadedUrl;
            }

            ProductDto dto = new ProductDto();
            dto.setName(name);
            dto.setCategoryId(categoryId);
            dto.setBrandId(brandId);
            dto.setSportId(sportId);
            dto.setGender(gender != null && !gender.isEmpty() ? gender : "Unisex");
            dto.setBasePrice(basePrice);
            dto.setSalePrice(salePrice);
            if (saleStart != null && !saleStart.isEmpty()) dto.setSaleStart(java.time.LocalDateTime.parse(saleStart));
            if (saleEnd != null && !saleEnd.isEmpty()) dto.setSaleEnd(java.time.LocalDateTime.parse(saleEnd));
            dto.setThumbnailUrl(finalThumbnailUrl);
            dto.setDescription(description);
            dto.setIsActive(true);

            ResponseEntity<ApiResponse> response = productService.addProduct(dto);
            ApiResponse body = response.getBody();
            if (body != null && "SUCCESS".equals(body.getStatus())) {
                ra.addFlashAttribute("successMessage", "Thêm sản phẩm thành công");
            } else {
                ra.addFlashAttribute("errorMessage", body != null ? body.getMessage() : "Lỗi không xác định");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/update")
    public String productUpdateFormSubmit(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam Integer categoryId,
            @RequestParam Integer brandId,
            @RequestParam Integer sportId,
            @RequestParam(required = false) String gender,
            @RequestParam Long basePrice,
            @RequestParam(required = false) Long salePrice,
            @RequestParam(required = false) String saleStart,
            @RequestParam(required = false) String saleEnd,
            @RequestParam(required = false) String thumbnailUrl,
            @RequestParam(required = false) MultipartFile thumbnailFile,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String isActive,
            RedirectAttributes ra) {
        try {
            String finalThumbnailUrl = thumbnailUrl;
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String uploadedUrl = uploadFile(thumbnailFile);
                if (uploadedUrl != null) finalThumbnailUrl = uploadedUrl;
            }

            ProductDto dto = new ProductDto();
            dto.setName(name);
            dto.setCategoryId(categoryId);
            dto.setBrandId(brandId);
            dto.setSportId(sportId);
            dto.setGender(gender != null && !gender.isEmpty() ? gender : "Unisex");
            dto.setBasePrice(basePrice);
            dto.setSalePrice(salePrice);
            
            if (saleStart != null && !saleStart.isEmpty()) {
                try { dto.setSaleStart(java.time.LocalDateTime.parse(saleStart)); } catch(Exception ex) {}
            }
            if (saleEnd != null && !saleEnd.isEmpty()) {
                try { dto.setSaleEnd(java.time.LocalDateTime.parse(saleEnd)); } catch(Exception ex) {}
            }
            
            dto.setThumbnailUrl(finalThumbnailUrl);
            dto.setDescription(description);
            dto.setIsActive(!"false".equals(isActive));

            ResponseEntity<ApiResponse<Product>> response = productService.updateProduct(id, dto);
            ApiResponse<Product> body = response.getBody();
            if (body != null && "SUCCESS".equals(body.getStatus())) {
                ra.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công");
            } else {
                ra.addFlashAttribute("errorMessage", body != null ? body.getMessage() : "Lỗi cập nhật");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/products/" + id;
    }

    // ==================== REST APIS ====================
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<ApiResponse> listProduct() {
        return productService.listProduct();
    }

    @GetMapping("/api/category/{categoryId}")
    @ResponseBody
    public ResponseEntity<ApiResponse> listByCategory(@PathVariable Integer categoryId) {
        return productService.listByCategory(categoryId);
    }

    @GetMapping("/api/brand/{brandId}")
    @ResponseBody
    public ResponseEntity<ApiResponse> listByBrand(@PathVariable Integer brandId) {
        return productService.listByBrand(brandId);
    }

    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<ApiResponse> addProductApi(@Valid @RequestBody ProductDto dto) {
        return productService.addProduct(dto);
    }

    @PutMapping("/api/update/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Product>> updateProductApi(
            @PathVariable Integer id, @Valid @RequestBody ProductDto dto) {
        return productService.updateProduct(id, dto);
    }

    @DeleteMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Integer id) {
        return productService.deleteProduct(id);
    }

    @PutMapping("/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<ApiResponse<Product>> toggleProductStatus(@PathVariable Integer id) {
        return productRepository.findById(id).map(product -> {
            product.setIsActive(!product.getIsActive());
            productRepository.save(product);
            String action = product.getIsActive() ? "Hiện" : "Ẩn";
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", action + " sản phẩm thành công", product));
        }).orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>("ERROR", "Không tìm thấy sản phẩm", null)));
    }
}
