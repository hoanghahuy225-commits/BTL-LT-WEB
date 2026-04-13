package com.example.WebBanHang.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Sport;
import com.example.WebBanHang.repository.ProductRepository;
import com.example.WebBanHang.repository.SportRepository;

@Service
public class SportService {
    
    @Autowired
    private SportRepository repo;

    @Autowired
    private ProductRepository productRepo;

    public ResponseEntity<ApiResponse> addSport(Sport sport) {
        if (sport.getName() == null || sport.getName().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Tên môn thể thao không được để trống", null)
            );
        }
        String name = sport.getName().trim() ;
        if (repo.findByName(name) != null) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Tên môn thể thao đã tồn tại", null)
            );
        } 
        sport.setCreateAt(LocalDateTime.now()); 
        repo.save(sport);
        return ResponseEntity.ok().body(
            new ApiResponse<>("SUCCESS", "Thêm môn thể thao thành công", sport)
        );
    }

    public ResponseEntity<ApiResponse> updateSport(Integer id, Sport sport) {
        try {
            Sport existing = repo.findById(id).orElse(null);
            if (existing == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Môn thể thao không tồn tại", null));
            }
            if (sport.getName() == null || sport.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tên môn thể thao không được để trống", null));
            }
            String name = sport.getName().trim();
            Sport other = repo.findByName(name);
            if (other != null && !other.getId().equals(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tên môn thể thao đã tồn tại", null));
            }
            existing.setName(name);
            existing.setDescription(sport.getDescription());
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Cập nhật môn thể thao thành công", repo.save(existing)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Lỗi Server", null));
        }
    }

    public ResponseEntity<ApiResponse> deleteSport(Integer id) {
        try {
            if (!repo.existsById(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Môn thể thao không tồn tại", null));
            }
            // KIỂM TRA RÀNG BUỘC SẢN PHẨM
            if (productRepo.existsBySportId(id)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Không thể xóa: Đang có sản phẩm thuộc môn thể thao này", null));
            }
            repo.deleteById(id);
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Xóa môn thể thao thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Không thể xóa môn thể thao này", null));
        }
    }

    public List<Sport> getAllSports() {
        return repo.findAll();
    }  

    public ResponseEntity<ApiResponse> listSport(){
        return ResponseEntity.ok().body(
            new ApiResponse<>("SUCCESS", "Lấy danh sách môn thể thao thành công", repo.findAll())
        );
    }
}
