package com.example.WebBanHang.controller.client;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.WebBanHang.dto.ChatConversationDto;
import com.example.WebBanHang.dto.ChatMessageDto;
import com.example.WebBanHang.model.ChatConversation;
import com.example.WebBanHang.model.ChatMessage;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.service.ChatService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    @Autowired private ChatService chatService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Client gets their own conversation (or creates one).
     */
    @GetMapping("/my-conversation")
    public ResponseEntity<?> getMyConversation(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }

        ChatConversation conv = chatService.getOrCreateConversation(currentUser);
        return ResponseEntity.ok(toConvDto(conv));
    }

    /**
     * Get message history for a conversation.
     */
    @GetMapping("/history/{conversationId}")
    public ResponseEntity<?> getHistory(@PathVariable Long conversationId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }

        List<ChatMessage> messages = chatService.getConversationHistory(conversationId);
        List<ChatMessageDto> dtos = messages.stream()
                .map(this::toMsgDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Admin: Get all conversations.
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getAllConversations(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || "CLIENT".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body("Không có quyền");
        }

        List<ChatConversation> conversations = chatService.getAllConversations();
        List<ChatConversationDto> dtos = conversations.stream()
                .map(this::toConvDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Mark conversation messages as read.
     */
    @PostMapping("/mark-read/{conversationId}")
    public ResponseEntity<?> markAsRead(@PathVariable Long conversationId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }

        chatService.markAsRead(conversationId);
        return ResponseEntity.ok("OK");
    }

    private ChatConversationDto toConvDto(ChatConversation conv) {
        ChatConversationDto dto = new ChatConversationDto();
        dto.setId(conv.getId());
        dto.setClientId(conv.getClient().getId());
        dto.setClientName(conv.getClient().getFullName());
        dto.setLastMessageContent(conv.getLastMessageContent());
        dto.setLastMessageAt(conv.getLastMessageAt() != null ? conv.getLastMessageAt().format(FMT) : null);
        dto.setUnreadCount(conv.getUnreadCount());
        dto.setStatus(conv.getStatus());
        return dto;
    }

    private ChatMessageDto toMsgDto(ChatMessage msg) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(msg.getId());
        dto.setConversationId(msg.getConversation().getId());
        dto.setSenderId(msg.getSender().getId());
        dto.setSenderName(msg.getSender().getFullName());
        dto.setSenderRole(msg.getSenderRole());
        dto.setContent(msg.getContent());
        dto.setTimestamp(msg.getTimestamp().format(FMT));
        dto.setStatus(msg.getStatus());
        return dto;
    }

    /**
     * Admin: Get total unread count.
     */
    @GetMapping("/admin/unread-count")
    public ResponseEntity<?> getAdminUnreadCount(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || "CLIENT".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body(-1);
        }
        return ResponseEntity.ok(chatService.getAdminUnreadCount());
    }
}
