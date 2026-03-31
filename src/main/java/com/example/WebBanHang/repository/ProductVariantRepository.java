package com.example.WebBanHang.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.example.WebBanHang.model.ProductVariant;
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    ProductVariant findByProductIdAndSizeIdAndColorId(Integer id , Integer sizeId , Integer colorId) ; 
    List<ProductVariant> findByProductId (Integer id ) ;
    @org.springframework.data.jpa.repository.Query("SELECT p.stockQuantity FROM ProductVariant p WHERE p.id = :id")
    Integer getStockQuantity (@org.springframework.data.repository.query.Param("id") Integer id) ; 
}
  