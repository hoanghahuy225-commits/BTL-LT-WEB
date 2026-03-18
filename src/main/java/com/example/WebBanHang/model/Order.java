package com.example.WebBanHang.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "order_code", length = 20, unique = true, nullable = false)
    private String orderCode;

    @Column(name = "order_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime orderDate;

    // THÔNG TIN GIAO HÀNG (Snapshot)
    @Column(name = "shipping_recipient_name", length = 100, nullable = false)
    private String shippingRecipientName;

    @Column(name = "shipping_phone", length = 20, nullable = false)
    private String shippingPhone;

    @Column(name = "shipping_address_line", length = 255, nullable = false)
    private String shippingAddressLine;

    @Column(name = "shipping_ward", length = 100)
    private String shippingWard;

    @Column(name = "shipping_city", length = 100, nullable = false)
    private String shippingCity;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    // THÔNG TIN THANH TOÁN
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "shipping_fee")
    private Long shippingFee = 0L;

    @Column(name = "discount_amount")
    private Long discountAmount = 0L;

    @Column(name = "final_amount", nullable = false)
    private Long finalAmount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; 

    @Column(name = "payment_status")
    private String paymentStatus = "Pending"; 

    @Column(name = "order_status")
    private String orderStatus = "Pending";

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationship can be mapped when User entity is available
    // @ManyToOne
    // @JoinColumn(name = "user_id", insertable = false, updatable = false)
    // private User user;
    
    // Relationship with OrderItem
    // @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<OrderItem> items;
}
