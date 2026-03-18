package com.example.WebBanHang.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    // Cart info
    private Integer cartId;
    private Integer quantity;

    // Product info
    private Integer productId;
    private String productName;
    private String thumbnailUrl;
    private Long basePrice;
    private Long salePrice;

    // Variant info
    private Integer variantId;
    private Integer stockQuantity;

    // Color info
    private Integer colorId;
    private String colorName;
    private String colorCode;   

    // Size info
    private Integer sizeId;
    private String sizeName;
}
