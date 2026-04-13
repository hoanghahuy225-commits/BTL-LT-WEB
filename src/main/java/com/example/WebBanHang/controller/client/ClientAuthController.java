package com.example.WebBanHang.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.dto.LoginDto;
import com.example.WebBanHang.dto.UserRegistrationDto;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ClientAuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() { return "client/login"; }

    @GetMapping("/register")
    public String showRegisterPage() { return "client/register"; }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> login(
            @RequestBody @Valid LoginDto loginDto,
            BindingResult bindingResult,
            HttpSession session) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors())
                errorMsg.append(error.getDefaultMessage()).append("; ");
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", errorMsg.toString(), null));
        }
        return userService.login(loginDto.getIdentifier(), loginDto.getPassword(), session);
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> register(
            @RequestBody @Valid UserRegistrationDto dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors())
                errorMsg.append(error.getDefaultMessage()).append("; ");
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", errorMsg.toString(), null));
        }
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setGender(dto.getGender());
        user.setIsActive(true);
        user.setRole("CLIENT");
        return userService.register(user);
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> logout(HttpSession session) {
        return userService.logout(session);
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {
        userService.logout(session);
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "client/profile";
    }

    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> updateProfile(@RequestBody User updatedData, HttpSession session) {
        return userService.updateProfile(updatedData, session);
    }

    @PostMapping("/profile/change-password")
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> changePassword(@RequestBody java.util.Map<String, String> passwords, HttpSession session) {
        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");
        return userService.changePassword(oldPassword, newPassword, session);
    }
}
