package com.example.WebBanHang.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.service.SportService;

@Controller
@RequestMapping("sport")
public class SportController {

    @Autowired
    private SportService sportService;

    @GetMapping("list")
    @ResponseBody
    public ResponseEntity<ApiResponse> listSport() {
        return sportService.listSport();
    }
}
