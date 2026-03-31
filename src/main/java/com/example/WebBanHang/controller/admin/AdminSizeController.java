package com.example.WebBanHang.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Size;
import com.example.WebBanHang.service.SizeService;

@RestController
@RequestMapping("/admin/sizes")
public class AdminSizeController {

    @Autowired
    private SizeService sizeService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addSize(@RequestBody Size size) {
        return sizeService.addSize(size);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateSize(@PathVariable Integer id, @RequestBody Size size) {
        return sizeService.updateSize(id, size);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteSize(@PathVariable Integer id) {
        return sizeService.deleteSize(id);
    }

    @PutMapping("/reorder")
    public ResponseEntity<ApiResponse> reorderSize(@RequestBody List<Integer> ids) {
        return sizeService.reorderSize(ids);
    }
}
