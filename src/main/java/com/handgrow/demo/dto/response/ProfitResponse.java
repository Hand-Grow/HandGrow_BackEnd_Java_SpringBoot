package com.handgrow.demo.dto.response;

import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfitResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalExpense;
    private BigDecimal profit;
    private Map<String, BigDecimal> expenseBreakdown;
}
