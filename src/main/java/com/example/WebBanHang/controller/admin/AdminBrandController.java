package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Brand;
import com.example.WebBanHang.service.BrandService;

@RestController
@RequestMapping("/admin/brands")
public class AdminBrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addBrand(@RequestBody Brand brand) {
        return brandService.addBrand(brand);
    }
}
