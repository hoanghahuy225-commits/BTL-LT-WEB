package com.example.WebBanHang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WebBanHang.model.CustomerAddress;

public interface  CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> {
     List<CustomerAddress> findByCustomerId(Integer customerId); 
}
