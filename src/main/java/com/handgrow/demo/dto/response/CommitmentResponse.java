package com.handgrow.demo.dto.response;

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
public class CommitmentResponse {
    private UUID id;
    private String farmerName;
    private String plotName;
    private BigDecimal quantity;
    private LocalDateTime createdAt;
}
