package com.example.WebBanHang.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.WebBanHang.model.Color;
import com.example.WebBanHang.model.Size;
import com.example.WebBanHang.repository.ColorRepository;
import com.example.WebBanHang.repository.SizeRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SizeRepository sizeRepo;

    @Autowired
    private ColorRepository colorRepo;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE order_items MODIFY variant_id INT NULL");
            System.out.println("✅ [DataInitializer] Patched order_items.variant_id to allow NULL");
        } catch (Exception e) {
            System.out.println("⚠️ [DataInitializer] Patch order_items.variant_id skipped: " + e.getMessage());
        }
        initSizes();
        initColors();
    }

    private void initSizes() {
        if (sizeRepo.count() > 0) return;

        List<Size> sizes = Arrays.asList(
            // Quần áo
            new Size(null, "XS",   1),
            new Size(null, "S",    2),
            new Size(null, "M",    3),
            new Size(null, "L",    4),
            new Size(null, "XL",   5),
            new Size(null, "XXL",  6),
            new Size(null, "XXXL", 7),
            // Giày (order = số size)
            new Size(null, "36", 36),
            new Size(null, "37", 37),
            new Size(null, "38", 38),
            new Size(null, "39", 39),
            new Size(null, "40", 40),
            new Size(null, "41", 41),
            new Size(null, "42", 42),
            new Size(null, "43", 43),
            new Size(null, "44", 44),
            new Size(null, "45", 45),
            new Size(null, "46", 46)
        );

        sizeRepo.saveAll(sizes);
        System.out.println("✅ [DataInitializer] Đã thêm " + sizes.size() + " kích cỡ mặc định");
    }

    private void initColors() {
        if (colorRepo.count() > 0) return;

        List<Color> colors = Arrays.asList(
            new Color(null, "Đen",        "#000000"),
            new Color(null, "Trắng",      "#FFFFFF"),
            new Color(null, "Đỏ",         "#FF0000"),
            new Color(null, "Xanh dương", "#0000FF"),
            new Color(null, "Xanh lá",    "#008000"),
            new Color(null, "Vàng",       "#FFFF00"),
            new Color(null, "Cam",        "#FF8C00"),
            new Color(null, "Xám",        "#808080"),
            new Color(null, "Hồng",       "#FF69B4"),
            new Color(null, "Tím",        "#800080")
        );
        colorRepo.saveAll(colors);
        System.out.println("✅ [DataInitializer] Đã thêm " + colors.size() + " màu sắc mặc định");
    }
}
