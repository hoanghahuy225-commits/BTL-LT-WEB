package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Color;
import com.example.WebBanHang.repository.ColorRepository;
import com.example.WebBanHang.service.ColorService;

@Controller
@RequestMapping("/admin/colors")
public class AdminColorController {

    @Autowired private ColorService colorService;
    @Autowired private ColorRepository colorRepository;

    // ==================== SSR ROUTES ====================
    @GetMapping({"", "/"})
    public String colorList(Model model) {
        model.addAttribute("activePage", "colors");
        model.addAttribute("pageTitle", "Màu sắc");
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("contentFragment", "admin/colors :: content");
        return "admin/layout";
    }

    // ==================== REST APIS ====================
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<ApiResponse> addColor(@RequestBody Color color) {
        return colorService.addColor(color);
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> updateColor(@PathVariable Integer id, @RequestBody Color color) {
        return colorService.updateColor(id, color);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> deleteColor(@PathVariable Integer id) {
        return colorService.deleteColor(id);
    }
}
