package com.handgrow.demo.dto.response;

import com.handgrow.demo.entity.enums.FeedTargetType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedItemResponse {
    private UUID id;
    private FeedTargetType type;
    private String title;
    private String content;
    private List<String> attachments;
    private String productName;
    private LocalDate expectedDate;
    private long likeCount;
    private long commentCount;
    private boolean isLiked;
    private LocalDateTime createdAt;
}
