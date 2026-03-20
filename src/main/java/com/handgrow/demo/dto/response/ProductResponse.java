package com.handgrow.demo.dto.response;

import com.handgrow.demo.entity.enums.ProductCategory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private UUID enterpriseId;
    private String enterpriseName;
    private String name;
    private ProductCategory category;
    private String unit;
    private BigDecimal basePrice;
    private String description;
    private String imageUrl;
    private Map<String, Object> attributes;
    private List<PriceTierResponse> priceTiers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceTierResponse {
        private Integer minQty;
        private BigDecimal price;
    }
}
