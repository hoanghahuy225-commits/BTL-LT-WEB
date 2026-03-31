package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.dto.UserRegistrationDto;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private UserService userService;

    /** GET /admin/me */
    @GetMapping("me")
    public ResponseEntity<ApiResponse<Object>> me(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(new ApiResponse<>("ERROR", "Chưa đăng nhập", null));
        }
        return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Thông tin admin", currentUser));
    }

    /** POST /admin/users/create-staff */
    @PostMapping("users/create-staff")
    public ResponseEntity<ApiResponse<Object>> createStaff(
            @RequestBody UserRegistrationDto dto,
            HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body(
                new ApiResponse<>("ERROR", "Chỉ Admin mới có quyền tạo tài khoản Staff", null));
        }
        User staff = new User();
        staff.setFullName(dto.getFullName());
        staff.setEmail(dto.getEmail());
        staff.setPhone(dto.getPhone());
        staff.setPassword(dto.getPassword());
        staff.setDateOfBirth(dto.getDateOfBirth());
        staff.setGender(dto.getGender());
        staff.setIsActive(true);
        staff.setRole("STAFF");
        return userService.registerStaff(staff);
    }
}
