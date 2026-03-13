package com.handgrow.demo.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBulkSaleRequest {
    private BigDecimal expectedPrice;
    private java.util.List<String> attachments;
}
