package com.example.WebBanHang.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.WebBanHang.model.ChatConversation;
import com.example.WebBanHang.model.ChatMessage;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.repository.ChatConversationRepository;
import com.example.WebBanHang.repository.ChatMessageRepository;

@Service
public class ChatService {

    @Autowired
    private ChatConversationRepository conversationRepository;

    @Autowired
    private ChatMessageRepository messageRepository;

    /**
     * Get or create an OPEN conversation for a client.
     */
    @Transactional
    public ChatConversation getOrCreateConversation(User client) {
        return conversationRepository.findByClientIdAndStatus(client.getId(), "OPEN")
                .orElseGet(() -> {
                    ChatConversation conv = new ChatConversation();
                    conv.setClient(client);
                    conv.setStatus("OPEN");
                    conv.setUnreadCount(0);
                    return conversationRepository.save(conv);
                });
    }

    /**
     * Save a new message and update the conversation's lastMessage info.
     */
    @Transactional
    public ChatMessage saveMessage(Long conversationId, User sender, String content) {
        ChatConversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        ChatMessage msg = new ChatMessage();
        msg.setConversation(conv);
        msg.setSender(sender);
        msg.setSenderRole(sender.getRole());
        msg.setContent(content);
        msg.setTimestamp(LocalDateTime.now());
        msg.setStatus("SENT");

        ChatMessage saved = messageRepository.save(msg);

        // Update conversation metadata
        conv.setLastMessageContent(content.length() > 500 ? content.substring(0, 500) : content);
        conv.setLastMessageAt(saved.getTimestamp());

        // If client sends a message, increment unread for admin
        if ("CLIENT".equals(sender.getRole())) {
            conv.setUnreadCount(conv.getUnreadCount() + 1);
        }

        conversationRepository.save(conv);
        return saved;
    }

    /**
     * Get all messages in a conversation ordered by timestamp.
     */
    public List<ChatMessage> getConversationHistory(Long conversationId) {
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }

    /**
     * Get all conversations sorted by most recent message (for Admin).
     */
    public List<ChatConversation> getAllConversations() {
        return conversationRepository.findAllByOrderByLastMessageAtDesc();
    }

    /**
     * Mark all messages in a conversation as READ and reset unread count.
     */
    @Transactional
    public void markAsRead(Long conversationId) {
        List<ChatMessage> messages = messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
        for (ChatMessage msg : messages) {
            if (!"READ".equals(msg.getStatus())) {
                msg.setStatus("READ");
            }
        }
        messageRepository.saveAll(messages);

        conversationRepository.findById(conversationId).ifPresent(conv -> {
            conv.setUnreadCount(0);
            conversationRepository.save(conv);
        });
    }

    /**
     * Find conversation by ID.
     */
    public ChatConversation findById(Long id) {
        return conversationRepository.findById(id).orElse(null);
    }

    /**
     * Get total unread count for Admin.
     */
    public int getAdminUnreadCount() {
        Integer sum = conversationRepository.sumTotalUnreadCount();
        return sum != null ? sum : 0;
    }
}
