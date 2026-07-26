package com.handgrow.demo.dto.request;

import com.handgrow.demo.entity.enums.ProductCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Category is required")
    private ProductCategory category;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than zero")
    private BigDecimal basePrice;

    private String description;
    private String imageUrl;
    private Map<String, Object> attributes;

    @Valid
    private List<PriceTierDto> priceTiers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceTierDto {
        @NotNull(message = "Minimum quantity is required")
        @Min(value = 1, message = "Minimum quantity must be at least 1")
        private Integer minQty;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        private BigDecimal price;
    }
}
