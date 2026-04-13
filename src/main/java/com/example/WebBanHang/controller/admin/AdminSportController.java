package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Sport;
import com.example.WebBanHang.repository.SportRepository;
import com.example.WebBanHang.service.SportService;

@Controller
@RequestMapping("/admin/sports")
public class AdminSportController {

    @Autowired private SportService sportService;
    @Autowired private SportRepository sportRepository;

    // ==================== SSR ROUTES ====================
    @GetMapping({"", "/"})
    public String sportList(Model model) {
        model.addAttribute("activePage", "sports");
        model.addAttribute("pageTitle", "Môn thể thao");
        model.addAttribute("sports", sportRepository.findAll());
        model.addAttribute("contentFragment", "admin/sports :: content");
        return "admin/layout";
    }

    // ==================== REST APIS ====================
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<ApiResponse> addSport(@RequestBody Sport sport) {
        return sportService.addSport(sport);
    }

    @PostMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> editSport(@PathVariable Integer id, @RequestBody Sport sport) {
        return sportService.updateSport(id, sport);
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse> deleteSport(@PathVariable Integer id) {
        return sportService.deleteSport(id);
    }
}
