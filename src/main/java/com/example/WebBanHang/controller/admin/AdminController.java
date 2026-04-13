package com.example.WebBanHang.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.dto.UserRegistrationDto;
import com.example.WebBanHang.model.*;
import com.example.WebBanHang.repository.*;
import com.example.WebBanHang.service.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private OrderRepository orderRepository;

    // ==================== DASHBOARD ====================
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");

        List<Order> allOrders = orderRepository.findAll();
        List<User> allClients = userRepository.findByRole("CLIENT");
        long productCount = productRepository.count();

        // Calculate Revenue (Only Delivered orders)
        long deliveredRevenue = allOrders.stream()
                .filter(o -> "Delivered".equals(o.getOrderStatus()))
                .mapToLong(o -> o.getFinalAmount() != null ? o.getFinalAmount() : 0L)
                .sum();

        model.addAttribute("clientCount", allClients.size());
        model.addAttribute("orderCount", allOrders.size());
        model.addAttribute("deliveredRevenue", deliveredRevenue);
        model.addAttribute("productCount", productCount);

        // Recent orders (last 5)
        List<Order> recentOrders = allOrders.stream()
                .sorted((a, b) -> {
                    if (a.getOrderDate() == null) return 1;
                    if (b.getOrderDate() == null) return -1;
                    return b.getOrderDate().compareTo(a.getOrderDate());
                })
                .limit(5)
                .toList();
        model.addAttribute("recentOrders", recentOrders);

        // Top 10 best selling products
        List<Product> bestSellers = productRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "soldQuantity"))
        ).getContent();
        model.addAttribute("bestSellingProducts", bestSellers);

        model.addAttribute("contentFragment", "admin/dashboard :: content");
        return "admin/layout";
    }
    // ==================== SEARCH ====================
    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("activePage", "search");
        model.addAttribute("pageTitle", "Kết quả tìm kiếm");
        model.addAttribute("query", query);

        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim();
            model.addAttribute("foundProducts", productRepository.findByNameContainingIgnoreCase(q));
            model.addAttribute("foundOrders", orderRepository.findByOrderCodeContainingIgnoreCaseOrShippingRecipientNameContainingIgnoreCase(q, q));
            model.addAttribute("foundBrands", brandRepository.findByNameContainingIgnoreCase(q));
            model.addAttribute("foundCategories", categoryRepository.findByNameContainingIgnoreCase(q));
        }

        model.addAttribute("contentFragment", "admin/search :: content");
        return "admin/layout";
    }

    // ==================== STAFF ====================
    @GetMapping("/staff")
    public String staffPage(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/admin?error=access-denied";
        }
        model.addAttribute("activePage", "staff");
        model.addAttribute("pageTitle", "Quản lý Nhân sự");
        // Lấy cả ADMIN và STAFF để hiển thị danh sách toàn bộ nhân sự
        model.addAttribute("staffList", userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()) || "STAFF".equals(u.getRole()))
                .toList());
        model.addAttribute("contentFragment", "admin/staff :: content");
        return "admin/layout";
    }

    /** POST /admin/users/create-staff */
    @PostMapping("/users/create-staff")
    @ResponseBody
    public ResponseEntity<?> createStaff(
            @RequestBody UserRegistrationDto dto,
            HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body(
                new ApiResponse<>("ERROR", "Chỉ Admin mới có quyền quản lý nhân sự", null));
        }
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setGender(dto.getGender());
        user.setIsActive(true);
        // Lấy role từ DTO, mặc định là STAFF nếu không chọn hoặc chọn sai
        String role = dto.getRole();
        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) {
            role = "STAFF";
        }
        user.setRole(role);
        
        return userService.registerStaff(user);
    }

    /** PUT /admin/users/{id}/toggle-status */
    @PutMapping("/users/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<?> toggleUserStatus(@PathVariable Integer id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        // CHỈ ADMIN mới có quyền khóa tài khoản
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body(new ApiResponse<>("ERROR", "Chỉ Admin mới có quyền thực hiện thao tác này", null));
        }
        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Bạn không thể tự khóa tài khoản của chính mình", null));
        }
        return userRepository.findById(id).map(user -> {
            boolean currentStatus = user.getIsActive() != null ? user.getIsActive() : true;
            user.setIsActive(!currentStatus);
            userRepository.save(user);
            String action = user.getIsActive() ? "Kích hoạt" : "Vô hiệu hóa";
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", action + " tài khoản thành công", user));
        }).orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>("ERROR", "Không tìm thấy người dùng", null)));
    }
}
