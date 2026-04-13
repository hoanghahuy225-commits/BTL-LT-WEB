package com.example.WebBanHang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WebBanHang.model.Category;

public interface CategoryRepository extends  JpaRepository<Category , Integer > {

    public List<Category> findAll() ;
    public Optional<Category> findById(Integer id);   
    public Category findByName(String name);  
    public List<Category> findByNameContainingIgnoreCase(String name);
} 
