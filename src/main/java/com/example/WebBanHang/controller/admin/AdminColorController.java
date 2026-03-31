package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Color;
import com.example.WebBanHang.service.ColorService;

@RestController
@RequestMapping("/admin/colors")
public class AdminColorController {

    @Autowired
    private ColorService colorService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addColor(@RequestBody Color color) {
        return colorService.addColor(color);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateColor(@PathVariable Integer id, @RequestBody Color color) {
        return colorService.updateColor(id, color);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteColor(@PathVariable Integer id) {
        return colorService.deleteColor(id);
    }
}
