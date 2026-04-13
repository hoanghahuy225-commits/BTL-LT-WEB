package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Brand;
import com.example.WebBanHang.repository.BrandRepository;
import com.example.WebBanHang.service.BrandService;

@Controller
@RequestMapping("/admin/brands")
public class AdminBrandController {

    @Autowired private BrandService brandService;
    @Autowired private BrandRepository brandRepository;

    // ==================== SSR ROUTES ====================
    @GetMapping({"", "/"})
    public String brandList(Model model) {
        model.addAttribute("activePage", "brands");
        model.addAttribute("pageTitle", "Thương hiệu");
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("contentFragment", "admin/brands :: content");
        return "admin/layout";
    }

    // ==================== REST APIS ====================
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<ApiResponse> addBrand(@RequestBody Brand brand) {
        return brandService.addBrand(brand);
    }

    @PostMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> editBrand(@PathVariable Integer id, @RequestBody Brand brand) {
        return brandService.updateBrand(id, brand);
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> deleteBrand(@PathVariable Integer id) {
        return brandService.deleteBrand(id);
    }
}
