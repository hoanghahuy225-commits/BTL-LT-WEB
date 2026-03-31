package com.example.WebBanHang.controller.client;

import com.example.WebBanHang.dto.OrderRequestDto;
import com.example.WebBanHang.model.Order;
import com.example.WebBanHang.model.OrderItem;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.service.*;
import com.example.WebBanHang.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired private ProductVariantService productVariantService;
    @Autowired private OrderService orderService;
    @Autowired private CategoryService categoryService;
    @Autowired private BrandService brandService;
    @Autowired private SportService sportService;
    @Autowired private ReviewService reviewService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addOrder(@RequestBody OrderRequestDto request) {
        try {
            Order savedOrder = orderService.createOrder(request);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Đặt hàng thành công");
            response.put("orderId", savedOrder.getOrderId());
            response.put("orderCode", savedOrder.getOrderCode());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
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

    @GetMapping("")
    public String orderHistory(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        List<Order> orders = orderService.getOrdersByUserId(currentUser.getId());
        Map<Integer, List<OrderItem>> orderItemsMap = new LinkedHashMap<>();
        Map<Integer, Boolean> orderReviewedMap = new HashMap<>();
        for (Order order : orders) {
            List<OrderItem> items = orderService.getOrderItems(order.getOrderId());
            orderItemsMap.put(order.getOrderId(), items);
            if ("Delivered".equals(order.getOrderStatus())) {
                Set<Integer> reviewedIds = reviewService.getReviewedProductIds(currentUser.getId(), order.getOrderId());
                long uniqueProductCount = items.stream().map(OrderItem::getProductId).filter(Objects::nonNull).distinct().count();
                orderReviewedMap.put(order.getOrderId(), uniqueProductCount > 0 && reviewedIds.size() >= uniqueProductCount);
            } else {
                orderReviewedMap.put(order.getOrderId(), false);
            }
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("orders", orders);
        model.addAttribute("orderItemsMap", orderItemsMap);
        model.addAttribute("orderReviewedMap", orderReviewedMap);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("sports", sportService.getAllSports());
        return "client/orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Integer id, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        Order order = orderService.getOrderById(id).orElse(null);
        if (order == null || !order.getUserId().equals(currentUser.getId())) return "redirect:/orders";
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("order", order);
        model.addAttribute("items", orderService.getOrderItems(id));
        model.addAttribute("reviewedProductIds", reviewService.getReviewedProductIds(currentUser.getId(), id));
        return "client/order_detail";
    }

    @PostMapping("/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            Map<String, Object> r = new HashMap<>();
            r.put("status", "ERROR"); r.put("message", "Vui lòng đăng nhập");
            return ResponseEntity.status(401).body(r);
        }
        try {
            Order order = orderService.getOrderById(id).orElse(null);
            if (order == null || !order.getUserId().equals(currentUser.getId())) {
                Map<String, Object> r = new HashMap<>();
                r.put("status", "ERROR"); r.put("message", "Đơn hàng không tồn tại");
                return ResponseEntity.badRequest().body(r);
            }
            if (!"Pending".equals(order.getOrderStatus())) {
                Map<String, Object> r = new HashMap<>();
                r.put("status", "ERROR"); r.put("message", "Chỉ có thể hủy đơn hàng khi ở trạng thái Chờ xác nhận");
                return ResponseEntity.badRequest().body(r);
            }
            return orderService.updateOrderStatus(id, "Cancelled");
        } catch (Exception e) {
            Map<String, Object> r = new HashMap<>();
            r.put("status", "ERROR"); r.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.internalServerError().body(r);
        }
    }
}
