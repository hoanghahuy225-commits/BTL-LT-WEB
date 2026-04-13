package com.example.WebBanHang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Brand;
import com.example.WebBanHang.repository.BrandRepository;
import com.example.WebBanHang.repository.ProductRepository;

@Service
public class BrandService {
    @Autowired
    private BrandRepository repo;

    @Autowired
    private ProductRepository productRepo;

    public ResponseEntity<ApiResponse> addBrand(Brand brand) {
        if (brand.getName() == null) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Tên thương hiệu không được để trống", null)
            ) ;
        }
        String name = brand.getName().trim() ;
        if (repo.findByName(name) != null) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Tên thương hiệu đã tồn tại", null)
            ) ;
        } 
        repo.save(brand) ; 
        return ResponseEntity.ok().body(
            new ApiResponse<>("SUCCESS", "Thêm thương hiệu thành công", brand)
        )   ; 
    }

    public ResponseEntity<ApiResponse> updateBrand(Integer id, Brand brand) {
        try {
            Brand existing = repo.findById(id).orElse(null);
            if (existing == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Thương hiệu không tồn tại", null));
            }
            if (brand.getName() == null || brand.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tên thương hiệu không được để trống", null));
            }
            String name = brand.getName().trim();
            Brand other = repo.findByName(name);
            if (other != null && !other.getId().equals(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tên thương hiệu đã tồn tại", null));
            }
            existing.setName(name);
            existing.setDescription(brand.getDescription());
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Cập nhật thương hiệu thành công", repo.save(existing)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Lỗi Server", null));
        }
    }

    public ResponseEntity<ApiResponse> deleteBrand(Integer id) {
        try {
            if (!repo.existsById(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Thương hiệu không tồn tại", null));
            }
            // KIỂM TRA RÀNG BUỘC SẢN PHẨM
            if (productRepo.existsByBrandId(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Không thể xóa: Đang có sản phẩm thuộc thương hiệu này", null));
            }
            repo.deleteById(id);
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Xóa thương hiệu thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Không thể xóa thương hiệu này", null));
        }
    }

    public List<Brand> getAllBrands() {
        return repo.findAll();
    } 

    public ResponseEntity<ApiResponse> listBrand() {
        try {
            return ResponseEntity.ok().body(
                new ApiResponse<>("SUCCESS", "Lấy danh sách thương hiệu thành công", repo.findAll())
            )   ; 
        } catch (Exception e) {
            return ResponseEntity.ok().body(
                new ApiResponse<>("ERROR", "Lỗi Server" , null ) 
             );
        }
    }  
}
