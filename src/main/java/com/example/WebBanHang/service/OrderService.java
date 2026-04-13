package com.example.WebBanHang.service;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.dto.CartItemDto;
import com.example.WebBanHang.dto.OrderRequestDto;
import com.example.WebBanHang.model.Order;
import com.example.WebBanHang.model.OrderItem;
import com.example.WebBanHang.model.Product;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.repository.OrderItemRepository;
import com.example.WebBanHang.repository.OrderRepository;
import com.example.WebBanHang.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ProductVariantService productVariantService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private com.example.WebBanHang.repository.ProductRepository productRepository;

    @Autowired
    private com.example.WebBanHang.repository.ProductImageRepository productImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Order createOrder(OrderRequestDto request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        LocalDateTime now = LocalDateTime.now();
        order.setOrderCode("DH" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());

        order.setShippingRecipientName(request.getShippingRecipientName() != null ? request.getShippingRecipientName() : "Không tên");
        order.setShippingPhone(request.getShippingPhone() != null ? request.getShippingPhone() : "Chưa có số DT");
        order.setShippingAddressLine(request.getShippingAddressLine() != null ? request.getShippingAddressLine() : "Chưa có địa chỉ");
        order.setShippingWard(request.getShippingWard());
        order.setShippingCity(request.getShippingCity() != null ? request.getShippingCity() : "");

        order.setNote(request.getNote());
        order.setTotalAmount(request.getTotalAmount() != null ? request.getTotalAmount() : 0L);
        order.setShippingFee(request.getShippingFee() != null ? request.getShippingFee() : 0L);
        order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : 0L);
        order.setFinalAmount(request.getFinalAmount() != null ? request.getFinalAmount() : 0L);

        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "COD");
        order.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : "Pending");
        order.setOrderStatus(request.getOrderStatus() != null ? request.getOrderStatus() : "Pending");

        Order savedOrder = orderRepository.save(order);

        if (request.getItems() != null) {
            for (CartItemDto item : request.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(savedOrder.getOrderId());
                
                // Đảm bảo productId luôn có giá trị
                Integer pId = item.getProductId();
                if (pId == null && item.getVariantId() != null) {
                    pId = productVariantService.getVariantById(item.getVariantId())
                            .map(v -> v.getProductId())
                            .orElse(null);
                }
                
                orderItem.setProductId(pId);
                orderItem.setVariantId(item.getVariantId());
                orderItem.setProductNameSnapshot(item.getProductName());

                String variantStr = "";
                if (item.getColorName() != null) variantStr += "Màu: " + item.getColorName();
                if (item.getSizeName() != null) {
                    if (!variantStr.isEmpty()) variantStr += ", ";
                    variantStr += "Size: " + item.getSizeName();
                }
                orderItem.setVariantSnapshot(variantStr);
            
                String thumbnailUrl = null;
                Product product = null;
                if (pId != null) {
                    product = productRepository.findById(pId).orElse(null);
                }
                
                if (product != null && item.getColorId() != null) {
                    thumbnailUrl = productImageRepository
                        .findFirstByProductIdAndColorId(product.getId(), item.getColorId())
                        .map(img -> img.getImageUrl())
                        .orElse(product.getThumbnailUrl());
                } else if (product != null) {
                    thumbnailUrl = product.getThumbnailUrl();
                } else {
                    thumbnailUrl = item.getThumbnailUrl();
                }
            
                orderItem.setProductImage(thumbnailUrl);

                int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
                orderItem.setQuantity(quantity);
                productVariantService.updateStockQuantity(item.getVariantId(), quantity, "subtract");

                Long price = item.getSalePrice() != null ? item.getSalePrice() : (item.getBasePrice() != null ? item.getBasePrice() : 0L);
                orderItem.setUnitPrice(price);
                orderItem.setTotalPrice(price * quantity);

                orderItemRepository.save(orderItem);

                if (item.getCartId() != null) {
                    cartService.removeCart(item.getCartId());
                }
            }
        }

        return savedOrder;
    }

    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Optional<Order> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Transactional
    public ResponseEntity<ApiResponse> updateOrderStatus(Integer orderId, String newStatus) {
        try {
            if (orderId == null || newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Thông tin không hợp lệ", null));
            }

            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                return ResponseEntity.status(404).body(new ApiResponse<>("ERROR", "Không tìm thấy đơn hàng", null));
            }

            Order order = orderOpt.get();
            String oldStatus = order.getOrderStatus();
            newStatus = newStatus.trim();

            if ("Cancelled".equals(oldStatus) && !"Cancelled".equals(newStatus)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Đơn hàng đã hủy không thể thay đổi trạng thái", null));
            }

            // Hoàn kho nếu hủy đơn
            if (!"Cancelled".equals(oldStatus) && "Cancelled".equals(newStatus)) {
                List<OrderItem> items = getOrderItems(orderId);
                for (OrderItem item : items) {
                    int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                    productVariantService.updateStockQuantity(item.getVariantId(), qty, "add");
                }
            }

            // Cập nhật SoldQuantity khi giao hàng thành công
            if ("Delivered".equals(newStatus) && !"Delivered".equals(oldStatus)) {
                List<OrderItem> items = getOrderItems(orderId);
                for (OrderItem item : items) {
                    Integer pId = item.getProductId();
                    // Nếu productId bị null, lấy từ variant
                    if (pId == null && item.getVariantId() != null && item.getVariantId() != 0) {
                        pId = productVariantService.getVariantById(item.getVariantId())
                                .map(v -> v.getProductId())
                                .orElse(null);
                    }

                    if (pId != null) {
                        Product p = productRepository.findById(pId).orElse(null);
                        if (p != null) {
                            int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                            int currentSold = p.getSoldQuantity() != null ? p.getSoldQuantity() : 0;
                            p.setSoldQuantity(currentSold + qty);
                            productRepository.save(p);
                        }
                    }
                }
            }

            order.setOrderStatus(newStatus);
            if ("Delivered".equals(newStatus) && "COD".equals(order.getPaymentMethod())) {
                order.setPaymentStatus("Paid");
            }

            orderRepository.save(order);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Cập nhật trạng thái thành công", order));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>("ERROR", "Lỗi server: " + e.getMessage(), null));
        }
    }

    public Page<Order> getAllOrdersPaginated(int page, int size, String[] sort, String status) {
        String sortField = "orderDate";
        Sort.Direction direction = Sort.Direction.DESC;

        if (sort != null && sort.length >= 2) {
            sortField = sort[0].trim();
            direction = sort[1].trim().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        } else if (sort != null && sort.length == 1 && sort[0].contains(",")) {
            String[] parts = sort[0].split(",", 2);
            sortField = parts[0].trim();
            if (parts.length > 1) {
                direction = parts[1].trim().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByOrderStatus(status, pageable);
        }
        
        return orderRepository.findAll(pageable);
    }

    public ResponseEntity<?> getAdminOrderDetail(Integer orderId) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ResponseEntity.status(404).body(new ApiResponse<>("ERROR", "Không tìm thấy đơn hàng", null));
            }
            List<OrderItem> items = getOrderItems(orderId);
            User user = userRepository.findById(order.getUserId()).orElse(null);

            Map<String, Object> detail = new HashMap<>();
            detail.put("order", order);
            detail.put("items", items);
            detail.put("user", user);

            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Lấy chi tiết thành công", detail));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>("ERROR", "Lỗi khi lấy chi tiết", e.getMessage()));
        }
    }

    public Map<String, Long> getOrderStatusCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("Total", orderRepository.count());
        counts.put("Pending", orderRepository.countByOrderStatus("Pending"));
        counts.put("Confirmed", orderRepository.countByOrderStatus("Confirmed"));
        counts.put("Shipping", orderRepository.countByOrderStatus("Shipping"));
        counts.put("Delivered", orderRepository.countByOrderStatus("Delivered"));
        counts.put("Cancelled", orderRepository.countByOrderStatus("Cancelled"));
        return counts;
    }
}
