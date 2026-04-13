package com.example.WebBanHang.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Category;
import com.example.WebBanHang.repository.CategoryRepository;
import com.example.WebBanHang.repository.ProductRepository;

@Service 
public class CategoryService {
    @Autowired
    private CategoryRepository repo;

    @Autowired
    private ProductRepository productRepo;

    public ResponseEntity<ApiResponse> listCategory() {
       try {
            return ResponseEntity.ok().body(
                new ApiResponse<>("SUCCESS", "Lấy danh sách danh mục thành công", repo.findAll())
            )   ; 
       } catch (Exception e) {
        return ResponseEntity.badRequest().body(
            new ApiResponse<>("ERROR", "Lỗi Server" , null ) 
         );  
       }
    }

    public List<Category> getAllCategories() {
        return repo.findAll();
    }

    public ResponseEntity<ApiResponse> addCategory(Category category) {
        try {
            String name = category.getName().trim() ;
            if (name.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tên danh mục không được để trống", null));
            } 
            if (repo.findByName(name) != null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tên danh mục đã tồn tại", null));
            }  
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Thêm danh mục thành công", repo.save(category))); 
       } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Lỗi Server" , null ));  
       }
    }

    public ResponseEntity<ApiResponse> updateCategory(Integer id, Category category) {
        try {
            Category existing = repo.findById(id).orElse(null);
            if (existing == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Danh mục không tồn tại", null));
            }
            String name = category.getName().trim();
            if (name.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tên danh mục không được để trống", null));
            }
            Category other = repo.findByName(name);
            if (other != null && !other.getId().equals(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tên danh mục đã tồn tại", null));
            }
            existing.setName(name);
            existing.setDescription(category.getDescription());
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Cập nhật danh mục thành công", repo.save(existing)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Lỗi Server", null));
        }
    }

    public ResponseEntity<ApiResponse> deleteCategory(Integer id) {
        try {
            if (!repo.existsById(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Danh mục không tồn tại", null));
            }
            // KIỂM TRA RÀNG BUỘC SẢN PHẨM
            if (productRepo.existsByCategoryId(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Không thể xóa: Đang có sản phẩm thuộc danh mục này", null));
            }
            repo.deleteById(id);
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Xóa danh mục thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Lỗi khi xóa danh mục", null));
        }
    }
}
