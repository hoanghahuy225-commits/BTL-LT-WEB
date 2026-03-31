package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.dto.ProductDto;
import com.example.WebBanHang.model.Product;
import com.example.WebBanHang.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("product")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("list")
    public ResponseEntity<ApiResponse> listProduct() {
        return productService.listProduct();
    }

    @GetMapping("category/{categoryId}")
    public ResponseEntity<ApiResponse> listByCategory(@PathVariable Integer categoryId) {
        return productService.listByCategory(categoryId);
    }

    @GetMapping("brand/{brandId}")
    public ResponseEntity<ApiResponse> listByBrand(@PathVariable Integer brandId) {
        return productService.listByBrand(brandId);
    }

    @PostMapping("add")
    public ResponseEntity<ApiResponse> addProduct(@Valid @RequestBody ProductDto dto) {
        return productService.addProduct(dto);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Integer id, @Valid @RequestBody ProductDto dto) {
        return productService.updateProduct(id, dto);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Integer id) {
        return productService.deleteProduct(id);
    }
}
