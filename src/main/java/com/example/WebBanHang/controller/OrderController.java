package com.example.WebBanHang.controller;

import com.example.WebBanHang.dto.OrderRequestDto;
import com.example.WebBanHang.model.Order;
import com.example.WebBanHang.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addOrder(@RequestBody OrderRequestDto request) {
        try {
            Order savedOrder = orderService.createOrder(request);
            
            // Return success response to the frontend
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Đặt hàng thành công");
            response.put("orderId", savedOrder.getOrderId());
            response.put("orderCode", savedOrder.getOrderCode());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Đã xảy ra lỗi khi tạo đơn hàng: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/success")
    public String orderSuccess(@RequestParam(name = "orderCode", required = false) String orderCode, Model model) {
        model.addAttribute("orderCode", orderCode);
        return "client/order_success";
    }
}
