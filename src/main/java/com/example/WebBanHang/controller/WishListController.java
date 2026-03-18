package com.example.WebBanHang.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.WebBanHang.dto.ProductSummaryDto;
import com.example.WebBanHang.model.Product;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.WishList;
import com.example.WebBanHang.service.WishListService;
import com.example.WebBanHang.service.SportService;
@Controller
@RequestMapping("wishlist")
public class WishListController {
    @Autowired
    private WishListService wishListService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SportService sportService;

    @Autowired
    private com.example.WebBanHang.service.CategoryService categoryService;

    @Autowired
    private com.example.WebBanHang.service.BrandService brandService;

    @PostMapping("add")
    @ResponseBody
    public ResponseEntity<ApiResponse<WishList>> addWishList(@RequestBody java.util.Map<String, Integer> payload) {
        wishListService.addWishList(payload.get("userId"), payload.get("productId"));
        return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Thêm vào danh sách yêu thích thành công", null));
    }

    @PostMapping("remove")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> removeWishList(@RequestBody java.util.Map<String, Integer> payload) {
        wishListService.removeWishList(payload.get("userId"), payload.get("productId"));
        return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Đã xóa khỏi danh sách yêu thích", null));
    }
    @GetMapping("")
    public  String  listWishList( @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "12") int size,
                                   HttpSession session, Model model) {
        User currentUser  = (User) session.getAttribute("currentUser") ;
        if(currentUser == null) {
            return "redirect:/login";
        }
        Integer userId  = (Integer) currentUser.getId();
        List<Product> products = wishListService.listWishList(userId)
                .stream()
                .map(item -> productService.getProduct(item.getProductId()))
                .filter(p -> p != null)
                .collect(Collectors.toList());
        List<ProductSummaryDto> listWishList = productService.listSummary(userId , products) ;
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sports",     sportService.getAllSports());
        model.addAttribute("brands",     brandService.getAllBrands());
        // Đổ dữ liệu cho Header
        
        model.addAttribute("wishlistProducts", listWishList);


        return "client/wishlist"; 
    }
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearWishList(HttpSession session  ){
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(new ApiResponse<>("ERROR", "Vui lòng đăng nhập", null));
        }
        Integer userId = (Integer) currentUser.getId();
        wishListService.clearWishList(userId);
        return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Đã xóa danh sách yêu thích", null));

    }
    @GetMapping("check-summary")
    @ResponseBody
    public ResponseEntity<?> checkSummaryDto(HttpSession   session ) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(new ApiResponse<>("ERROR", "Vui lòng đăng nhập", null));
        }

        Integer userId = (Integer) currentUser.getId();

        // Lấy danh sách WishList của user
        List<WishList> wishListItems = wishListService.listWishList(userId);

        // Chuyển đổi sang danh sách Product
        List<Product> products = wishListItems.stream()
                .map(item -> productService.getProduct(item.getProductId()))
                .filter(p -> p != null)
                .collect(Collectors.toList());

        // Lấy danh sách ProductSummaryDto
        List<ProductSummaryDto> listWishList = productService.listSummary(userId, products);

        // Trả về JSON để bạn kiểm tra trên Postman hoặc trình duyệt
        return ResponseEntity.ok(listWishList);
    }


}
