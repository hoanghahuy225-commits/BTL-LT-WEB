package com.example.WebBanHang.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.dto.CartItemDto;
import com.example.WebBanHang.model.Cart;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.service.CartService;
import com.example.WebBanHang.service.CategoryService;
import com.example.WebBanHang.service.BrandService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("cart")
public class CartController {

    @Autowired private CartService cartService;
    @Autowired private CategoryService categoryService;
    @Autowired private BrandService brandService;

    @GetMapping("")
    public String cart(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("cart", cartService.listCart(currentUser.getId()));
        return "client/cart";
    }

    @PostMapping("add")
    @ResponseBody
    public ResponseEntity<?> addCart(HttpSession session, @RequestBody Cart cart) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return ResponseEntity.status(401).body(new ApiResponse<>("ERROR", "Vui lòng đăng nhập", null));
        if (cart.getProductId() == null || cart.getQuantity() == null || cart.getQuantity() <= 0)
            return ResponseEntity.status(400).body(new ApiResponse<>("ERROR", "Số lượng không hợp lệ", null));
        return cartService.addCart(cart);
    }

    @DeleteMapping("remove/{id}")
    @ResponseBody
    public ResponseEntity<?> removeCart(HttpSession session, @PathVariable Integer id) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return ResponseEntity.status(401).body(new ApiResponse<>("ERROR", "Vui lòng đăng nhập", null));
        cartService.removeCart(id);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Đã xóa sản phẩm khỏi giỏ hàng", null));
    }

    @PutMapping("update")
    @ResponseBody
    public ResponseEntity<?> updateCart(HttpSession session, @RequestBody Cart cart) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return ResponseEntity.status(401).body(new ApiResponse<>("ERROR", "Vui lòng đăng nhập", null));
        if (cart.getQuantity() == null || cart.getQuantity() <= 0)
            return ResponseEntity.status(400).body(new ApiResponse<>("ERROR", "Số lượng phải lớn hơn 0", null));
        cartService.updateCart(cart);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Đã cập nhật giỏ hàng", null));
    }

    @GetMapping("data")
    @ResponseBody
    public ResponseEntity<?> getCartData(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return ResponseEntity.status(401).body(new ApiResponse<>("ERROR", "Vui lòng đăng nhập để xem giỏ hàng", null));
        List<CartItemDto> cartData = cartService.listCart(currentUser.getId());
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Lấy dữ liệu giỏ hàng thành công", cartData));
    }
}
