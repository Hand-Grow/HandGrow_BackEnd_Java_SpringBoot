package com.handgrow.demo.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftContractRequest {
    private BigDecimal agreedPrice;
    private BigDecimal agreedQuantity;
    private LocalDate deliveryDate;
    private String terms;
}
