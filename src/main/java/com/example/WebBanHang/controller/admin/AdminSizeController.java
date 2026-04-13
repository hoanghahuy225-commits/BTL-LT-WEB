package com.example.WebBanHang.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Size;
import com.example.WebBanHang.repository.SizeRepository;
import com.example.WebBanHang.service.SizeService;

@Controller
@RequestMapping("/admin/sizes")
public class AdminSizeController {

    @Autowired private SizeService sizeService;
    @Autowired private SizeRepository sizeRepository;

    // ==================== SSR ROUTES ====================
    @GetMapping({"", "/"})
    public String sizeList(Model model) {
        model.addAttribute("activePage", "sizes");
        model.addAttribute("pageTitle", "Kích thước");
        model.addAttribute("sizes", sizeRepository.findAllByOrderByOrderAsc());
        model.addAttribute("contentFragment", "admin/sizes :: content");
        return "admin/layout";
    }

    // ==================== REST APIS ====================
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<ApiResponse> addSize(@RequestBody Size size) {
        return sizeService.addSize(size);
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> updateSize(@PathVariable Integer id, @RequestBody Size size) {
        return sizeService.updateSize(id, size);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> deleteSize(@PathVariable Integer id) {
        return sizeService.deleteSize(id);
    }

    @PutMapping("/reorder")
    @ResponseBody
    public ResponseEntity<ApiResponse> reorderSize(@RequestBody List<Integer> ids) {
        return sizeService.reorderSize(ids);
    }
}
