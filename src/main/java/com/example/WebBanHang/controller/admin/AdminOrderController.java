package com.example.WebBanHang.controller.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.service.OrderService;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Lấy danh sách tất cả đơn hàng với phân trang và sắp xếp
     * API: GET /admin/orders?page=0&size=10&sort=orderDate,desc
     */
    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate,desc") String[] sort) {
        try {
            
            return ResponseEntity.ok(orderService.getAllOrdersPaginated(page, size, sort));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>("ERROR", "Lỗi khi lấy danh sách đơn hàng", e.getMessage()));
        }
    }
    // '
    // '

   
    @PutMapping("/{id}/update-status")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        return orderService.updateOrderStatus(id, newStatus);
    }
}
