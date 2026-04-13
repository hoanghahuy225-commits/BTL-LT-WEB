package com.example.WebBanHang.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "sport_id")
    private Integer sportId;

    @Column(name = "brand_id")
    private Integer brandId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "base_price", nullable = false)
    private Long basePrice;

    @Column(name = "sale_price") 
    private Long salePrice;

    @Column(name = "sale_start")
    private LocalDateTime saleStart;

    @Column(name = "sale_end")
    private LocalDateTime saleEnd;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "sold_quantity", columnDefinition = "INT DEFAULT 0")
    private Integer soldQuantity = 0;

    @Column(name = "review_count", columnDefinition = "INT DEFAULT 0")
    private Integer reviewCount = 0;

    @Column(name = "average_rating", columnDefinition = "DECIMAL(3,2) DEFAULT 0.00")
    private Double averageRating = 0.0;

    public enum Gender {
        Nam, Nu, Unisex
    }

}