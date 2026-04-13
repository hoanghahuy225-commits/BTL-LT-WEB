package com.example.WebBanHang.repository;

import com.example.WebBanHang.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByEmailAndRole(String email, String role);
    User findByUsernameAndRole(String username, String role);
    java.util.List<User> findByRole(String role);

    @Query("SELECT u.username FROM User u WHERE u.id = :userId")
    String findUsernameById(@Param("userId") Integer userId);
}
