package com.example.WebBanHang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private Long conversationId;
    private Integer senderId;
    private String senderName;
    private String senderRole;
    private String content;
    private String timestamp;
    private String status;
}
