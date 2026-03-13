package com.handgrow.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Schema(description = "Response DTO for sourcing request")
public class SourcingRequestResponse {

    @Schema(description = "Unique identifier of the sourcing request")
    private UUID id;

    @Schema(description = "Name of the product to source")
    private String productName;

    @Schema(description = "Quantity needed")
    private Double quantity;

    @Schema(description = "Unit of measurement")
    private String unit;

    @Schema(description = "Expected price in VND")
    private BigDecimal expectedPrice;

    @Schema(description = "Deadline for the sourcing request")
    private LocalDate deadline;

    @Schema(description = "Additional requirements")
    private String requirements;

    @Schema(description = "Current status of the request")
    private String status;

    @Schema(description = "Enterprise ID who created the request")
    private UUID enterpriseId;

    @Schema(description = "Enterprise name who created the request")
    private String enterpriseName;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
