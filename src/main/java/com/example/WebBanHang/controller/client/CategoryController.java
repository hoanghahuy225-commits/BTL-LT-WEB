package com.example.WebBanHang.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.service.CategoryService;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping
    public ResponseEntity<ApiResponse> listCategory() {
        return service.listCategory();
    }
}
