package com.example.WebBanHang.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.service.SizeService;

@RestController
@RequestMapping("size")
public class SizeController {

    @Autowired
    private SizeService sizeService;

    @GetMapping("list")
    public ResponseEntity<ApiResponse> listSize() {
        return sizeService.listSize();
    }
}
