package com.example.WebBanHang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChatConversationDto {
    private Long id;
    private Integer clientId;
    private String clientName;
    private String lastMessageContent;
    private String lastMessageAt;
    private Integer unreadCount;
    private String status;
}
