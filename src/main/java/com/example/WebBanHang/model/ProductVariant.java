package com.example.WebBanHang.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "product_variants") 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant { 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Integer id; 
    @Column(name = "product_id", nullable = false)
    private Integer productId; 
    @Column(name = "size_id", nullable = true)
    private Integer sizeId; 
    @Column(name = "color_id", nullable = true)
    private Integer colorId; 
    @Column(name = "stock_quantity")
    private Integer stockQuantity;  
    @Column(name = "created_at")
    private LocalDateTime createdAt; 
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; 
}
