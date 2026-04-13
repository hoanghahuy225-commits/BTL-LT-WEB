package com.example.WebBanHang.controller.client;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.WebBanHang.dto.ChatMessageDto;
import com.example.WebBanHang.model.ChatConversation;
import com.example.WebBanHang.model.ChatMessage;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.repository.UserRepository;
import com.example.WebBanHang.service.ChatService;

@Controller
public class ChatController {

    @Autowired private ChatService chatService;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private UserRepository userRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Client sends a message.
     * Payload: { senderId, conversationId, content }
     */
    @MessageMapping("/chat.send")
    public void handleClientMessage(@Payload ChatMessageDto payload, SimpMessageHeaderAccessor headerAccessor) {
        User sender = userRepository.findById(payload.getSenderId()).orElse(null);
        if (sender == null) return;

        Long conversationId = payload.getConversationId();
        ChatConversation conv;

        if (conversationId == null || conversationId == 0) {
            conv = chatService.getOrCreateConversation(sender);
            conversationId = conv.getId();
        }

        ChatMessage saved = chatService.saveMessage(conversationId, sender, payload.getContent());

        ChatMessageDto response = toDto(saved);

        // Send to the conversation topic (client is subscribed here)
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, response);

        // Notify all admins about new message
        messagingTemplate.convertAndSend("/topic/admin/new-message", response);
    }

    /**
     * Admin/Staff sends a reply.
     * Payload: { senderId, conversationId, content }
     */
    @MessageMapping("/chat.admin.send")
    public void handleAdminMessage(@Payload ChatMessageDto payload) {
        User sender = userRepository.findById(payload.getSenderId()).orElse(null);
        if (sender == null) return;

        Long conversationId = payload.getConversationId();
        if (conversationId == null) return;

        ChatMessage saved = chatService.saveMessage(conversationId, sender, payload.getContent());
        ChatMessageDto response = toDto(saved);

        // Send to the conversation topic (client is subscribed here)
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, response);

        // Notify all admins about the reply
        messagingTemplate.convertAndSend("/topic/admin/new-message", response);
    }

    private ChatMessageDto toDto(ChatMessage msg) {
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
}
