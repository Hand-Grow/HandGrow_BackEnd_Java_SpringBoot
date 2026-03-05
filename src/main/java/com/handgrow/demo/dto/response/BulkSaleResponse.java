package com.handgrow.demo.dto.response;

import com.handgrow.demo.entity.enums.BulkSaleStatus;
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
public class BulkSaleResponse {
    private UUID id;
    private String productName;
    private BigDecimal totalQuantity;
    private BigDecimal expectedPrice;
    private BulkSaleStatus status;
    private String coopName;
    private LocalDateTime createdAt;
}
