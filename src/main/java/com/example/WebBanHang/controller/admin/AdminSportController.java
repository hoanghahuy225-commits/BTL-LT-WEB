package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Sport;
import com.example.WebBanHang.service.SportService;

@RestController
@RequestMapping("/admin/sports")
public class AdminSportController {

    @Autowired
    private SportService sportService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addSport(@RequestBody Sport sport) {
        return sportService.addSport(sport);
    }
}
