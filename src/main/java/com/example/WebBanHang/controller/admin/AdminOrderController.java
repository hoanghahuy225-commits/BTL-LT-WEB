package com.example.WebBanHang.controller.admin;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Order;
import com.example.WebBanHang.service.OrderService;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // ==================== SSR ROUTES ====================
    @GetMapping({"", "/"})
    public String orderList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            Model model) {
        model.addAttribute("activePage", "orders");
        model.addAttribute("pageTitle", "Đơn hàng");

        Page<Order> orderPage = orderService.getAllOrdersPaginated(page, 10, new String[]{"orderDate", "desc"}, status);
        List<Order> orders = orderPage.getContent();

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalElements", orderPage.getTotalElements());
        model.addAttribute("statusFilter", status);
        
       
        model.addAttribute("statusCounts", orderService.getOrderStatusCounts());

        model.addAttribute("contentFragment", "admin/orders :: content");
        return "admin/layout";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("activePage", "orders");
        model.addAttribute("pageTitle", "Chi tiết đơn hàng");
        
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isEmpty()) return "redirect:/admin/orders";

        model.addAttribute("order", orderOpt.get());
        model.addAttribute("items", orderService.getOrderItems(id));
        model.addAttribute("contentFragment", "admin/order-detail :: content");
        return "admin/layout";
    }

    @PutMapping("/{id}/update-status")
    @ResponseBody
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        return orderService.updateOrderStatus(id, newStatus);
    }
}
