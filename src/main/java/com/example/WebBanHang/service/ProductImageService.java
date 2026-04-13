package com.example.WebBanHang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.ProductImage;
import com.example.WebBanHang.repository.ProductImageRepository;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    public ResponseEntity<ApiResponse<ProductImage>> addProductImage(ProductImage productImage) {
        try {
            ProductImage savedImage = productImageRepository.save(productImage);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Thêm ảnh sản phẩm thành công", savedImage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "Lỗi khi thêm ảnh sản phẩm: " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ApiResponse<List<ProductImage>>> addProductImageList(List<ProductImage> productImages) {
        try {
            List<ProductImage> savedImages = productImageRepository.saveAll(productImages);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Thêm danh sách ảnh thành công", savedImages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "Lỗi khi thêm danh sách ảnh: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<List<ProductImage>>> updateProductImageList(Integer productId, List<ProductImage> productImages) {
        try {
            // Xóa toàn bộ ảnh cũ của sản phẩm này
            productImageRepository.deleteAllByProductId(productId);
            // Lưu danh sách ảnh mới
            List<ProductImage> savedImages = productImageRepository.saveAll(productImages);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Cập nhật danh sách ảnh thành công", savedImages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "Lỗi khi cập nhật danh sách ảnh: " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ApiResponse<Void>> deleteProductImage(Integer id) {
        try {
            if (!productImageRepository.existsById(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Ảnh không tồn tại", null));
            }
            productImageRepository.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Xóa ảnh thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "Lỗi khi xóa ảnh: " + e.getMessage(), null));
        }
    }
}
