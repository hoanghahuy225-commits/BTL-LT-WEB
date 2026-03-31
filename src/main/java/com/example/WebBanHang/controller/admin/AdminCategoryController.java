package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Category;
import com.example.WebBanHang.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService service;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category category) {
        return service.addCategory(category);
    }
}
