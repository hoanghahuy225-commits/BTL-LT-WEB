package com.example.WebBanHang.service;

import java.time.LocalDateTime;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.WebBanHang.model.WishList;
import com.example.WebBanHang.repository.WishListRepository;
@Service
public class WishListService {
    
    @Autowired
    private WishListRepository wishListRepository; 

    public void addWishList(Integer userId, Integer productId) {
        List<Integer> existingIds = getWishListProductIds(userId);
        if (!existingIds.contains(productId)) {
            WishList wishList = new WishList(userId, productId, LocalDateTime.now());
            wishListRepository.save(wishList); 
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void removeWishList(Integer userId, Integer productId) {
        wishListRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public java.util.List<Integer> getWishListProductIds(Integer userId) {
        return wishListRepository.findProductIdsByUserId(userId);
    }

    public List<WishList> listWishList(Integer userId) {
        return wishListRepository.findByUserId(userId);
    }
    public void clearWishList(Integer userId) {
        wishListRepository.deleteByUserId(userId);
    }
}
