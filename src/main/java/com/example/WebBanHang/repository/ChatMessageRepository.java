package com.example.WebBanHang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WebBanHang.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByConversationIdOrderByTimestampAsc(Long conversationId);

    long countByConversationIdAndStatusNot(Long conversationId, String status);
}
