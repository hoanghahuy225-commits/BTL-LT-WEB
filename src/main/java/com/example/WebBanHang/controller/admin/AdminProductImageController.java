package com.example.WebBanHang.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.ProductImage;
import com.example.WebBanHang.service.ProductImageService;
import com.example.WebBanHang.repository.ProductImageRepository;

@RestController
@RequestMapping("product-image")
public class AdminProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductImageRepository productImageRepository;

    @GetMapping("{productId}")
    public ResponseEntity<ApiResponse<List<ProductImage>>> getByProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Lấy ảnh thành công", productImageRepository.findAllByProductId(productId)));
    }

    @PostMapping("add")
    public ResponseEntity<ApiResponse<List<ProductImage>>> addProductImage(
            @RequestBody List<ProductImage> productImage) {
        return productImageService.addProductImageList(productImage);
    }
    @PutMapping("update/{id}")
    public ResponseEntity<ApiResponse<List<ProductImage>>> updateProductImage(
            @PathVariable Integer id,
            @RequestBody List<ProductImage> productImage) {
        return productImageService.updateProductImageList(id, productImage);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(@PathVariable Integer id) {
        return productImageService.deleteProductImage(id);
    }
}
