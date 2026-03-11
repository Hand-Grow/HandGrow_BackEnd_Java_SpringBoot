package com.handgrow.demo.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampaignRequest {
    private String productName;
    private String title;
    private String content;
    private List<String> attachments;
    private LocalDate expectedDate;
}
