package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Category;
import com.example.WebBanHang.repository.CategoryRepository;
import com.example.WebBanHang.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired private CategoryService service;
    @Autowired private CategoryRepository categoryRepository;

    // ==================== SSR ROUTES ====================
    @GetMapping({"", "/"})
    public String categoryList(Model model) {
        model.addAttribute("activePage", "categories");
        model.addAttribute("pageTitle", "Danh mục");
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("contentFragment", "admin/categories :: content");
        return "admin/layout";
    }

    // ==================== REST APIS ====================
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category category) {
        return service.addCategory(category);
    }

    @PostMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> editCategory(@PathVariable Integer id, @RequestBody Category category) {
        return service.updateCategory(id, category);
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Integer id) {
        return service.deleteCategory(id);
    }
}
