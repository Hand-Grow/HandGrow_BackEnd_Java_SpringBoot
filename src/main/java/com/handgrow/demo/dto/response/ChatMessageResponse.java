package com.handgrow.demo.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String id;
    private UUID senderId;
    private String senderType;
    private String senderName; // Name of Coop or Enterprise
    private String senderAvatarUrl; // optional avatar URL to show in UI
    private String content;
    private LocalDateTime createdAt;
}
