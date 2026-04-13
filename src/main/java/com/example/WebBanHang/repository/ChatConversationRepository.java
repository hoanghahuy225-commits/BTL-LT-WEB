package com.example.WebBanHang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WebBanHang.model.ChatConversation;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    Optional<ChatConversation> findByClientIdAndStatus(Integer clientId, String status);

    List<ChatConversation> findAllByOrderByLastMessageAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT SUM(c.unreadCount) FROM ChatConversation c")
    Integer sumTotalUnreadCount();
}
