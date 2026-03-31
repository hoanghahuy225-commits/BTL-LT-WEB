package com.example.WebBanHang.service;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.Order;
import com.example.WebBanHang.model.OrderItem;
import com.example.WebBanHang.model.Product;
import com.example.WebBanHang.model.ProductReview;
import com.example.WebBanHang.repository.OrderItemRepository;
import com.example.WebBanHang.repository.OrderRepository;
import com.example.WebBanHang.repository.ProductRepository;
import com.example.WebBanHang.repository.ProductReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired private ProductReviewRepository reviewRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ProductRepository ProductRepository;


    /**
     * Lấy Set<productId> mà user đã review trong order này.
     * Dùng để render trạng thái "Đã đánh giá" / "Đánh giá" ở UI.
     */
    public Set<Integer> getReviewedProductIds(Integer userId, Integer orderId) {
        return reviewRepository.findByUserIdAndOrderId(userId, orderId)
                .stream()
                .map(ProductReview::getProductId)
                .collect(Collectors.toSet());
    }

    /**
     * Gửi đánh giá sản phẩm.
     * Rules:
     *   1. Đơn hàng phải Delivered
     *   2. User phải là chủ đơn
     *   3. Product phải có trong đơn
     *   4. Chưa đánh giá product này trong đơn này
     */
    public ResponseEntity<ApiResponse<ProductReview>> submitReview(
            Integer userId, Integer orderId, Integer productId,
            Integer rating, String comment) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null)
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Đơn hàng không tồn tại", null));
            if (!order.getUserId().equals(userId))
                return ResponseEntity.status(403).body(new ApiResponse<>("ERROR", "Bạn không có quyền đánh giá đơn hàng này", null));
            if (!"Delivered".equals(order.getOrderStatus()))
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Chỉ có thể đánh giá sau khi đơn hàng được giao thành công", null));

            List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
            boolean productInOrder = items.stream().anyMatch(i -> productId.equals(i.getProductId()));
            if (!productInOrder)
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Sản phẩm không thuộc đơn hàng này", null));

            boolean alreadyReviewed = reviewRepository
                    .findByUserIdAndOrderIdAndProductId(userId, orderId, productId).isPresent();
            if (alreadyReviewed)
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Bạn đã đánh giá sản phẩm này rồi", null));
            Optional<Product> p = ProductRepository.findById(productId) ; 
            p.ifPresent(product -> {
                double avgPresent = product.getAverageRating();
                int oldReviewCount = product.getReviewCount();
                double newRating = rating;
                int newReviewCount = oldReviewCount + 1;
                double newAverage = ((avgPresent * oldReviewCount) + newRating) / newReviewCount;
                 product.setReviewCount(newReviewCount);
                     
                     newAverage = Math.round(newAverage * 10.0) / 10.0;
                     product.setAverageRating(newAverage);
                     ProductRepository.save(product); 

            }) ; 
            ProductReview review = new ProductReview();
            review.setUserId(userId);
            review.setOrderId(orderId);
            review.setProductId(productId);
            review.setRating(rating);
            review.setComment(comment);
            ProductReview saved = reviewRepository.save(review);

            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Đánh giá thành công", saved));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>("ERROR", "Lỗi server: " + e.getMessage(), null));
        }
    }
}
