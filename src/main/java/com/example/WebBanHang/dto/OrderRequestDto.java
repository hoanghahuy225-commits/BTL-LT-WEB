package com.example.WebBanHang.dto;

import lombok.Data;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

@Data
public class OrderRequestDto {
    @jakarta.validation.constraints.NotNull(message = "Người dùng không được để trống")
    private Integer userId; 
    @NotBlank(message = "Người nhận không được để trống")
    private String shippingRecipientName;
    @NotBlank(message = "Số điện thoại không được để trống")
    private String shippingPhone;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String shippingAddressLine;
    private String shippingWard;
    private String shippingCity;
    private String note;
    private Long totalAmount;
    private Long shippingFee;
    private Long discountAmount;
    private Long finalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private List<CartItemDto> items;
}
