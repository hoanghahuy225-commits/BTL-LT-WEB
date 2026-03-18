package com.example.WebBanHang.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.WebBanHang.dto.CartItemDto;
import com.example.WebBanHang.model.Color;
import com.example.WebBanHang.model.CustomerAddress;
import com.example.WebBanHang.model.Product;
import com.example.WebBanHang.model.ProductVariant;
import com.example.WebBanHang.model.Size;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.repository.ColorRepository;
import com.example.WebBanHang.repository.ProductImageRepository;
import com.example.WebBanHang.repository.ProductRepository;
import com.example.WebBanHang.repository.ProductVariantRepository;
import com.example.WebBanHang.repository.SizeRepository;
import com.example.WebBanHang.service.CartService;
import com.example.WebBanHang.service.CategoryService;
import com.example.WebBanHang.service.BrandService;
import com.example.WebBanHang.service.CustomerAddressService;
import com.example.WebBanHang.service.SportService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerAddressService customerAddressService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;
    @Autowired
    private SportService sportService; 
 
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @GetMapping("")
    public String checkout(
            @RequestParam(required = false) List<Integer> cartIds,
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer variantId,
            @RequestParam(required = false, defaultValue = "1") Integer qty,
            HttpSession session,
            Model model) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("sports", sportService.getAllSports()); 

        List<CartItemDto> items = new ArrayList<>();

        // Luồng 1: Từ giỏ hàng
        if (cartIds != null && !cartIds.isEmpty()) {
            items = cartService.getCartItemsByIds(currentUser.getId(), cartIds);
            model.addAttribute("source", "cart");
        }
        // Luồng 2: Mua ngay từ trang sản phẩm
        else if (productId != null) {
            CartItemDto item = buildBuyNowItem(productId, variantId, qty);
            if (item != null) items.add(item);
            model.addAttribute("source", "buynow");
        }

        // Tính tổng tiền
        long totalPrice = items.stream()
                .mapToLong(i -> {
                    long price = (i.getSalePrice() != null ? i.getSalePrice() : i.getBasePrice());
                    return price * (i.getQuantity() != null ? i.getQuantity() : 1);
                }).sum();

        long shippingFee = (totalPrice >= 1000000) ? 0 : 30000;
        long finalPrice = totalPrice + shippingFee;

        model.addAttribute("items", items);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("finalPrice", finalPrice);

        // Lấy danh sách địa chỉ đã lưu của user
        List<CustomerAddress> addresses = customerAddressService.getCustomerAddresses(currentUser.getId());
        model.addAttribute("addresses", addresses);
        model.addAttribute("customerId", currentUser.getId());

        return "client/checkout";
    }

    private CartItemDto buildBuyNowItem(Integer productId, Integer variantId, Integer qty) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return null;

        CartItemDto item = new CartItemDto();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setBasePrice(product.getBasePrice());
        item.setSalePrice(product.getSalePrice());
        item.setQuantity(qty);

        if (variantId != null) {
            ProductVariant variant = productVariantRepository.findById(variantId).orElse(null);
            if (variant != null) {
                item.setVariantId(variant.getId());
                item.setStockQuantity(variant.getStockQuantity());

                Color color = colorRepository.findById(variant.getColorId()).orElse(null);
                if (color != null) {
                    item.setColorId(color.getId());
                    item.setColorName(color.getName());
                    item.setColorCode(color.getCode());
                }

                Size size = sizeRepository.findById(variant.getSizeId()).orElse(null);
                if (size != null) {
                    item.setSizeId(size.getId());
                    item.setSizeName(size.getName());
                }

                // Thumbnail theo màu
                String thumbnailUrl = productImageRepository
                        .findFirstByProductIdAndColorId(product.getId(), variant.getColorId())
                        .map(img -> img.getImageUrl())
                        .orElse(product.getThumbnailUrl());
                item.setThumbnailUrl(thumbnailUrl);
            }
        } else {
            item.setThumbnailUrl(product.getThumbnailUrl());
        }

        return item;
    }
}
