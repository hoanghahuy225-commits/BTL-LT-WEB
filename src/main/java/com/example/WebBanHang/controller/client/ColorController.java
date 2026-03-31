package com.example.WebBanHang.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.service.ColorService;

@RestController
@RequestMapping("color")
public class ColorController {

    @Autowired
    private ColorService colorService;

    @GetMapping("list")
    public ResponseEntity<ApiResponse> listColor() {
        return colorService.listColor();
    }
}
