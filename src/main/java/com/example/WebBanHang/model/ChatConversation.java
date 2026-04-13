package com.example.WebBanHang.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat_conversations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(name = "last_message_content", length = 500)
    private String lastMessageContent;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "unread_count")
    private Integer unreadCount = 0;

    @Column(name = "status", length = 20)
    private String status = "OPEN"; // OPEN, CLOSED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.lastMessageAt == null) {
            this.lastMessageAt = this.createdAt;
        }
    }
}
