package com.handgrow.demo.dto.request;

import com.handgrow.demo.entity.enums.ProductCategory;
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
    private String name;
    private ProductCategory category;
    private String unit;
    private BigDecimal basePrice;
    private String description;
    private String imageUrl;
    private Map<String, Object> attributes;
    private List<PriceTierDto> priceTiers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceTierDto {
        private Integer minQty;
        private BigDecimal price;
    }
}
