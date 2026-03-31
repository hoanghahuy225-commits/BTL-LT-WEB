package com.example.WebBanHang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WebBanHang.model.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    List<ProductReview> findAllByProductId(Integer productId);

    /** Kiểm tra 1 user đã review 1 product trong 1 order cụ thể chưa */
    Optional<ProductReview> findByUserIdAndOrderIdAndProductId(Integer userId, Integer orderId, Integer productId);

    /** Lấy tất cả review của 1 user trong 1 order (để hiển thị trạng thái đã review) */
    List<ProductReview> findByUserIdAndOrderId(Integer userId, Integer orderId);
}
