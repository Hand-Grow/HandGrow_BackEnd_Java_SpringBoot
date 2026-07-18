package com.handgrow.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "Request DTO for creating a new sourcing request")
public class CreateSourcingRequest {

    @NotBlank(message = "Product name is required")
    @Schema(description = "Name of the product to source", example = "Cà phê Arabica")
    private String productName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Schema(description = "Quantity needed", example = "1000")
    private Double quantity;

    @NotBlank(message = "Unit is required")
    @Schema(
            description = "Unit of measurement",
            example = "kg",
            allowableValues = {"kg", "tấn"})
    private String unit;

    @Schema(description = "Expected price in VND", example = "50000")
    private BigDecimal expectedPrice;

    @NotNull(message = "Deadline is required")
    @Schema(description = "Deadline for the sourcing request", example = "2024-12-31")
    private LocalDate deadline;

    @Schema(description = "Additional requirements", example = "Chất lượng cao, không thuốc trừ sâu")
    private String requirements;
}
