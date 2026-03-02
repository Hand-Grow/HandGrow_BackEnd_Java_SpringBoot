package com.handgrow.demo.dto.response;

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
public class PlotResponse {
    private UUID id;
    private String name;
    private String location;
    private Double area;
    private String areaUnit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
