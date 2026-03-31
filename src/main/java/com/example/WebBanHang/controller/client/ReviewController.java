package com.example.WebBanHang.controller.client;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.ProductReview;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.repository.ProductRepository;
import com.example.WebBanHang.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.WebBanHang.model.Order;
import com.example.WebBanHang.model.Product;
import com.example.WebBanHang.service.OrderService;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository ProductRepository;


    /**
     * GET /reviews/order/{id}
     * Render trang chứa danh sách các sản phẩm trong đơn hàng để đánh giá
     */
    @GetMapping("/order/{id}")
    public String reviewOrderPage(@PathVariable Integer id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(id).orElse(null);
        if (order == null || !order.getUserId().equals(currentUser.getId())) {
            return "redirect:/orders"; // Không có quyền
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("order", order);
        java.util.List<com.example.WebBanHang.model.OrderItem> rawItems = orderService.getOrderItems(id);
        java.util.Map<Integer, java.util.Map<String, Object>> groupedItems = new java.util.LinkedHashMap<>();
        
        for (com.example.WebBanHang.model.OrderItem item : rawItems) {
            if (item.getProductId() == null) continue;
            Integer pId = item.getProductId();
            
            Map<String, Object> map = groupedItems.computeIfAbsent(pId, k -> {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("productId", pId);
                m.put("productNameSnapshot", item.getProductNameSnapshot());
                m.put("productImage", item.getProductImage());
                m.put("totalQuantity", 0);
                m.put("totalPrice", 0L);
                m.put("variants", new ArrayList<String>());
                return m;
            });
            
            int qty = item.getQuantity() != null ? item.getQuantity() : 1;
            long price = item.getTotalPrice() != null ? item.getTotalPrice() : 0L;
            
            map.put("totalQuantity", ((Integer) map.get("totalQuantity")) + qty);
            map.put("totalPrice", ((Long) map.get("totalPrice")) + price);
            
            String variantDesc = (item.getVariantSnapshot() != null && !item.getVariantSnapshot().isEmpty()) ? item.getVariantSnapshot() : "Mặc định";
            variantDesc += " (x" + qty + ")";
            @SuppressWarnings("unchecked")
            java.util.List<String> variantsList = (java.util.List<String>) map.get("variants");
            variantsList.add(variantDesc);
        }
        
        model.addAttribute("items", new java.util.ArrayList<>(groupedItems.values()));
        model.addAttribute("reviewedProductIds", reviewService.getReviewedProductIds(currentUser.getId(), id));
        
        return "client/review_order";
    }

    /**
     * POST /reviews/submit
     * Body: { orderId, productId, rating (1-5), comment }
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ProductReview>> submitReview(
            @RequestBody Map<String, Object> body,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(new ApiResponse<>("ERROR", "Vui lòng đăng nhập", null));
        }

        Integer orderId   = null;
        Integer productId = null;
        Integer  rating    = null;
        String  comment   = null;
        try {
            if (body.get("orderId") != null) orderId = Integer.parseInt(body.get("orderId").toString());
            if (body.get("productId") != null) productId = Integer.parseInt(body.get("productId").toString());
            if (body.get("rating") != null) rating =  Integer.parseInt(body.get("rating").toString());
            if (body.get("comment") != null) comment = body.get("comment").toString();
            
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Dữ liệu không hợp lệ: " + e.getMessage(), null));
        }

        if (orderId == null || productId == null || rating == null || rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Dữ liệu bắt buộc bị thiếu hoặc không hợp lệ", null));
        }

        try {
            return reviewService.submitReview(currentUser.getId(), orderId, productId, rating, comment);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse<>("ERROR", "Đã xảy ra lỗi: " + e.getMessage(), null));
        }
    }
}
