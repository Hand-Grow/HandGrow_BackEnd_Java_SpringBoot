package com.handgrow.demo.dto.response;

import com.handgrow.demo.entity.enums.ActivityType;
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
public class DiaryResponse {
    private UUID id;
    private String plotName;
    private LocalDate activityDate;
    private ActivityType activityType;
    private BigDecimal expense;
    private String aiExtractedData;
    private String originalTranscript;
}
