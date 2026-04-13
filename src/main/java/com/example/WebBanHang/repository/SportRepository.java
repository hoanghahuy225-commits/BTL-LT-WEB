package com.example.WebBanHang.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WebBanHang.model.Sport;

public interface SportRepository extends JpaRepository<Sport, Integer>  {
    public Sport findByName(String name); 
} 
