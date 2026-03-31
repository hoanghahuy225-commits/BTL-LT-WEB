package com.example.WebBanHang.controller.client;

import com.example.WebBanHang.service.*;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ProductDetailDto;
import com.example.WebBanHang.dto.ProductSummaryDto;
import com.example.WebBanHang.model.Brand;
import com.example.WebBanHang.model.Category;
import com.example.WebBanHang.model.Product;
import com.example.WebBanHang.model.Sport;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.repository.ColorRepository;
import com.example.WebBanHang.repository.SizeRepository;

@Controller
public class HomeController {

    @Autowired private CategoryService categoryService;
    @Autowired private ProductVariantService productVariantService;
    @Autowired private SportService sportService;
    @Autowired private ProductService productService;
    @Autowired private BrandService brandService;
    @Autowired private WishListService wishListService;
    @Autowired private ColorRepository colorRepository;
    @Autowired private SizeRepository sizeRepository;

    @RequestMapping("/")
    public String home(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) model.addAttribute("currentUser", currentUser);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sports", sportService.getAllSports());
        model.addAttribute("brands", brandService.getAllBrands());
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSummaryDto> summaryPage = productService.listSummaryPaginated(
            currentUser != null ? currentUser.getId() : null, pageable);
        model.addAttribute("products", summaryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", summaryPage.getTotalPages());
        return "client/index";
    }

    @GetMapping("/product/{id}")
    public String showProduct(@PathVariable Integer id, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) model.addAttribute("currentUser", currentUser);
        ProductDetailDto dto = productService.getProductDetail(id, currentUser != null ? currentUser.getId() : null);
        model.addAttribute("product", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sports", sportService.getAllSports());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("variant", productVariantService.getAllProductVariants(id));
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAllByOrderByOrderAsc());
        return "client/product";
    }

    @GetMapping("/filter")
    public String filterProducts(
            @RequestParam(required = false) List<Integer> categoryId,
            @RequestParam(required = false) List<Integer> sportId,
            @RequestParam(required = false) List<Integer> brandId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) model.addAttribute("currentUser", currentUser);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sports", sportService.getAllSports());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("selectedCategoryIds", categoryId);
        model.addAttribute("selectedSportIds", sportId);
        model.addAttribute("selectedBrandIds", brandId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSummaryDto> resultPage = productService.filterProducts(
                currentUser != null ? currentUser.getId() : null,
                categoryId, sportId, brandId, sortBy, keyword, minPrice, maxPrice, pageable);
        model.addAttribute("products", resultPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", resultPage.getTotalPages());
        model.addAttribute("totalElements", resultPage.getTotalElements());
        return "client/filter";
    }

    @GetMapping("/api/test/home-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testHomeData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpSession session) {
        Map<String, Object> responseData = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");
        responseData.put("currentUser", currentUser);
        responseData.put("categories", categoryService.getAllCategories());
        responseData.put("sports", sportService.getAllSports());
        responseData.put("brands", brandService.getAllBrands());
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSummaryDto> summaryPage = productService.listSummaryPaginated(
                currentUser != null ? currentUser.getId() : null, pageable);
        Map<String, Object> paginationInfo = new HashMap<>();
        paginationInfo.put("products", summaryPage.getContent());
        paginationInfo.put("currentPage", page);
        paginationInfo.put("totalPages", summaryPage.getTotalPages());
        paginationInfo.put("totalElements", summaryPage.getTotalElements());
        paginationInfo.put("isFirst", summaryPage.isFirst());
        paginationInfo.put("isLast", summaryPage.isLast());
        responseData.put("productPagination", paginationInfo);
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/api/test/product/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testProductData(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> responseData = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");
        responseData.put("currentUser_ID", currentUser != null ? currentUser.getId() : "Chưa đăng nhập");
        ProductDetailDto dto = productService.getProductDetail(id, currentUser != null ? currentUser.getId() : null);
        responseData.put("product", dto);
        responseData.put("variants", productVariantService.getAllProductVariants(id));
        return ResponseEntity.ok(responseData);
    }
}
