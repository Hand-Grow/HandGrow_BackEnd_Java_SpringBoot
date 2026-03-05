package com.handgrow.demo.dto.response;

import com.handgrow.demo.entity.enums.OfferStatus;
import java.math.BigDecimal;
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
public class OfferResponse {
    private UUID id;
    private String enterpriseName;
    private BigDecimal offeredPrice;
    private String message;
    private OfferStatus status;
    private LocalDateTime createdAt;
}
