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
public class ChatRoomResponse {
    private String id;
    private UUID bulkSaleId;
    private String productName;
    private UUID cooperativeId;
    private String cooperativeName;
    private String cooperativeAvatarUrl; // optional avatar of coop
    private UUID enterpriseId;
    private String enterpriseName;
    private String enterpriseAvatarUrl; // optional avatar of enterprise
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
