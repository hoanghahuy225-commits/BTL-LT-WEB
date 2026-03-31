package com.example.WebBanHang.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.ProductVariant;
import com.example.WebBanHang.service.ProductVariantService;

@RestController
@RequestMapping("variant")
public class AdminProductVariantController {

    @Autowired
    private ProductVariantService variantService;

    @GetMapping("/{id}")
    public List<ProductVariant> listVariant(@PathVariable Integer id) {
        return variantService.getAllProductVariants(id);
    }

    @PostMapping("add")
    public ResponseEntity<ApiResponse<ProductVariant>> addVariant(@RequestBody ProductVariant variant) {
        return variantService.addProductVariant(variant);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ApiResponse<ProductVariant>> updateVariant(
            @PathVariable Integer id, @RequestBody ProductVariant variant) {
        return variantService.updateVariant(id, variant);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<ApiResponse<ProductVariant>> deleteVariant(@PathVariable Integer id) {
        return variantService.deleteVariant(id);
    }
}
