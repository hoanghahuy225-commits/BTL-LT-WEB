package com.example.WebBanHang.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.WebBanHang.dto.ApiResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("api/upload")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "File trống", null));
            }

            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg"; // fallback
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                String contentType = file.getContentType();
                if (contentType != null) {
                    if (contentType.equals("image/png")) extension = ".png";
                    else if (contentType.equals("image/gif")) extension = ".gif";
                    else if (contentType.equals("image/webp")) extension = ".webp";
                }
            }
            String newFilename = UUID.randomUUID().toString() + extension;
            
            Path filePath = Paths.get(UPLOAD_DIR, newFilename);
            Files.write(filePath, file.getBytes());

            String fileUrl = "/uploads/" + newFilename;
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Tải ảnh lên thành công", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Lỗi tải ảnh: " + e.getMessage(), null));
        }
    }
}
