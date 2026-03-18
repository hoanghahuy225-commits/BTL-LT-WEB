package com.example.WebBanHang.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.dto.CartItemDto;
import com.example.WebBanHang.model.Cart;
import com.example.WebBanHang.model.Color;
import com.example.WebBanHang.model.Product;
import com.example.WebBanHang.model.ProductVariant;
import com.example.WebBanHang.model.Size;
import com.example.WebBanHang.repository.CartRepository;
import com.example.WebBanHang.repository.ColorRepository;
import com.example.WebBanHang.repository.ProductImageRepository;
import com.example.WebBanHang.repository.ProductRepository;
import com.example.WebBanHang.repository.ProductVariantRepository;
import com.example.WebBanHang.repository.SizeRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    /** Trả về danh sách giỏ hàng dưới dạng DTO đầy đủ thông tin */
    public List<CartItemDto> listCart(Integer userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        return carts.stream().map(cart -> {
            CartItemDto dto = new CartItemDto();
            dto.setCartId(cart.getId());
            dto.setQuantity(cart.getQuantity());

            // Product
            Product product = productRepository.findById(cart.getProductId()).orElse(null);
            if (product != null) {
                dto.setProductId(product.getId());
                dto.setProductName(product.getName());
                
                dto.setBasePrice(product.getBasePrice());
                dto.setSalePrice(product.getSalePrice());
            }
            else {
                return null;
            }

            // Variant → lấy colorId và sizeId
            ProductVariant variant = productVariantRepository.findById(cart.getVariantId()).orElse(null);
            if (variant != null) {
                dto.setVariantId(variant.getId());
                dto.setStockQuantity(variant.getStockQuantity());

                // Color
                Color color = colorRepository.findById(variant.getColorId()).orElse(null);
                if (color != null) {
                    dto.setColorId(color.getId());
                    dto.setColorName(color.getName());
                    dto.setColorCode(color.getCode());
                }

                // Size
                Size size = sizeRepository.findById(variant.getSizeId()).orElse(null);
                if (size != null) {
                    dto.setSizeId(size.getId());
                    dto.setSizeName(size.getName());
                }
            }


            // Lấy ảnh theo màu, fallback về thumbnailUrl của product
            String thumbnailUrl = null;
            if (product != null && dto.getColorId() != null) {
                thumbnailUrl = productImageRepository
                    .findFirstByProductIdAndColorId(product.getId(), dto.getColorId())
                    .map(img -> img.getImageUrl())
                    .orElse(product.getThumbnailUrl());
            } else if (product != null) {
                thumbnailUrl = product.getThumbnailUrl();
            }
            dto.setThumbnailUrl(thumbnailUrl);

            return dto;
        }).collect(Collectors.toList());
    }

    public ResponseEntity<ApiResponse<Object>> addCart(Cart cart) {
        if (cart.getQuantity() <= 0) {
            return ResponseEntity.status(400).body(new ApiResponse<>("ERROR", "Số lượng không hợp lệ", null));
        }
        if (cart.getProductId() == null) {
            return ResponseEntity.status(400).body(new ApiResponse<>("ERROR", "Sản phẩm không hợp lệ", null));
        }
        if (cart.getUserId() == null) {
            return ResponseEntity.status(400).body(new ApiResponse<>("ERROR", "Người dùng không hợp lệ", null));
        }
        if (cart.getQuantity() > productVariantRepository.getStockQuantity(cart.getVariantId())) {
            return ResponseEntity.status(400).body(new ApiResponse<>("ERROR", "Số lượng hàng không đủ để ", null));
        }
        Integer userId = cart.getUserId();
        Integer productId = cart.getProductId();
        Integer variantId = cart.getVariantId();
        Integer quantity = cart.getQuantity();
        Optional<Cart> existingCart = cartRepository.findByUserIdAndProductIdAndVariantId(userId, productId, variantId);
        if (existingCart.isPresent()) {
            existingCart.get().setQuantity(existingCart.get().getQuantity() + quantity);
            cartRepository.save(existingCart.get());
            return ResponseEntity.status(200).body(new ApiResponse<>("SUCCESS", "Thêm vào giỏ hàng thành công", null));
        }
        

        cartRepository.save(cart);
        return ResponseEntity.status(200).body(new ApiResponse<>("SUCCESS", "Thêm vào giỏ hàng thành công", null));
    }

    public void removeCart(Integer id) {
        cartRepository.deleteById(id);
    }

    public void updateCart(Cart cart) {
        Cart existingCart = cartRepository.findById(cart.getId()).orElse(null);
        if (existingCart != null) {
            existingCart.setQuantity(cart.getQuantity());
            cartRepository.save(existingCart);
        }
    }

    /** Lọc các item theo danh sách cartId (dùng cho trang checkout) */
    public List<CartItemDto> getCartItemsByIds(Integer userId, List<Integer> cartIds) {
        return listCart(userId).stream()
                .filter(item -> item != null && cartIds.contains(item.getCartId()))
                .collect(Collectors.toList());
    }
}
