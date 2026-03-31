package com.example.WebBanHang.repository;

import com.example.WebBanHang.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserIdOrderByOrderDateDesc(Integer userId);
    Optional<Order> findById(Integer id);
   
}
