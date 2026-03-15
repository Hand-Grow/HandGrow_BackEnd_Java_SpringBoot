package com.handgrow.demo.dto.request;

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
public class CreateGroupBuyCampaignRequest {
    private UUID productId;
    private String title;
    private String description;
    private LocalDate deadlineDate;
}
