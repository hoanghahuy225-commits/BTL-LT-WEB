package com.example.WebBanHang.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.WebBanHang.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    public List<Product> findAll();
    public List<Product> findByCategoryId(Integer categoryId);
    public List<Product> findBySportId(Integer sportId);
    public List<Product> findByBrandId(Integer brandId);
    public List<Product> findByIsActiveTrue();
    public List<Product> findByNameContainingIgnoreCase(String keyword);
    public Page<Product> findByIsActiveTrue(Pageable pageable);

    boolean existsByCategoryId(Integer categoryId);
    boolean existsByBrandId(Integer brandId);
    boolean existsBySportId(Integer sportId);
}
