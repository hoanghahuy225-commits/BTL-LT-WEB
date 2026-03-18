package com.example.WebBanHang.service;

import com.example.WebBanHang.dto.CartItemDto;
import com.example.WebBanHang.dto.OrderRequestDto;
import com.example.WebBanHang.model.Order;
import com.example.WebBanHang.model.OrderItem;
import com.example.WebBanHang.repository.OrderItemRepository;
import com.example.WebBanHang.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    public Order createOrder(OrderRequestDto request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        LocalDateTime now = LocalDateTime.now();  
        // Generate a random 8-character order code
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
                orderItem.setVariantId(item.getVariantId() != null ? item.getVariantId() : 0);
                orderItem.setProductNameSnapshot(item.getProductName());
                
                String variantStr = "";
                if (item.getColorName() != null) variantStr += "Màu: " + item.getColorName();
                if (item.getSizeName() != null) {
                    if (!variantStr.isEmpty()) variantStr += ", ";
                    variantStr += "Size: " + item.getSizeName();
                }
                orderItem.setVariantSnapshot(variantStr);
                
                int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
                orderItem.setQuantity(quantity);
                productVariantService.updateStockQuantity(item.getVariantId(), quantity); 
                
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
}
