package com.handgrow.demo.dto.response;

import com.handgrow.demo.entity.enums.GroupBuyCampaignStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyCampaignResponse {
    private UUID id;
    private UUID cooperativeId;
    private String cooperativeName;
    private UUID productId;
    private String productName;
    private String productImageUrl;
    private String title;
    private String description;
    private GroupBuyCampaignStatus status;
    private LocalDate deadlineDate;
    private BigDecimal totalCommittedQty;
    private BigDecimal currentUnitPrice;
    private Integer participationCount;
    private Double progressPercent;
    private String nextTierLabel;
}
