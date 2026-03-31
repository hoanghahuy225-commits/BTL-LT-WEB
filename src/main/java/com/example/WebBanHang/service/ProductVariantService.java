package com.example.WebBanHang.service;

import java.time.LocalDateTime;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.ProductVariant;
import com.example.WebBanHang.repository.ProductVariantRepository;
@Service
public class ProductVariantService {
        @Autowired
        private ProductVariantRepository productVariantRepository; 
        public List<ProductVariant>  getAllProductVariants(Integer id ) {
            try {
                return productVariantRepository.findByProductId(id);
            } catch (Exception e) {
                return null ; 
            }
        }  

         public ResponseEntity <ApiResponse<ProductVariant>> addProductVariant(ProductVariant p) {
            try {
                if(p.getProductId() == null || p.getSizeId() == null || p.getColorId() == null || p.getStockQuantity() == null) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>( "ERROR", "Thiếu thông tin", null));  
                }   

                if(productVariantRepository.findByProductIdAndSizeIdAndColorId(p.getProductId(), p.getSizeId(), p.getColorId()) != null) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>( "ERROR", "Variant đã tồn tại", null));  
                }
                p.setCreatedAt(LocalDateTime.now());
                p.setUpdatedAt(LocalDateTime.now());
                return ResponseEntity.ok(new ApiResponse<>( "SUCCESS", "Thêm variant sản phẩm thành công ", productVariantRepository.save(p))); 
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new ApiResponse<>( "ERROR", "Lỗi Server", null)); 
            }
         }
         public ResponseEntity<ApiResponse<ProductVariant>> updateVariant(Integer id , ProductVariant  p ) {
            try {
                if(p.getProductId() == null || p.getSizeId() == null || p.getColorId() == null || p.getStockQuantity() == null) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>( "ERROR", "Thiếu thông tin", null));  
                }   

                if(!productVariantRepository.existsById(id)) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>( "ERROR", "Variant không tồn tại", null));  
                }
                if(productVariantRepository.findByProductIdAndSizeIdAndColorId(p.getProductId(), p.getSizeId(), p.getColorId()) != null) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>( "ERROR", "Variant đã tồn tại", null));  
                }
                p.setId(id);
                p.setUpdatedAt(LocalDateTime.now());
                return ResponseEntity.ok(new ApiResponse<>( "SUCCESS", "Cập nhật variant sản phẩm thành công ", productVariantRepository.save(p))); 
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new ApiResponse<>( "ERROR", "Lỗi Server", null)); 
            }
         }
         
        public ResponseEntity<ApiResponse<ProductVariant>> deleteVariant(Integer id ) {
            try {
               

                if(!productVariantRepository.existsById(id)) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>( "ERROR", "Variant không tồn tại", null));  
                }
                productVariantRepository.deleteById(id);
                return ResponseEntity.ok(new ApiResponse<>( "SUCCESS", "Xóa variant sản phẩm thành công ", null)); 
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new ApiResponse<>( "ERROR", "Lỗi Server", null)); 
            }
         }

         public java.util.Optional<ProductVariant> getVariantById(Integer id) {
            return productVariantRepository.findById(id);
         }

         public void updateStockQuantity(Integer variantId, int quantity , String type ) {
            if (variantId == null) return;
            ProductVariant variant = productVariantRepository.findById(variantId).orElse(null);
            if (variant != null) {
                if(type.equals("add")) {
                    variant.setStockQuantity(variant.getStockQuantity() + quantity);
                } else if(type.equals("subtract")) {
                    variant.setStockQuantity(variant.getStockQuantity() - quantity);
                }
                productVariantRepository.save(variant);
            }
         } 
}
