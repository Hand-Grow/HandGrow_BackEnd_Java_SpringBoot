package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.BulkSaleStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "bulk_sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkSale extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coop_id", nullable = false)
    private Cooperative cooperative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private CollectionCampaign campaign;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "total_quantity", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalQuantity;

    @Column(name = "expected_price", precision = 15, scale = 2)
    private BigDecimal expectedPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private BulkSaleStatus status = BulkSaleStatus.OPEN;

    @Column(name = "attachments", columnDefinition = "jsonb")
    @Convert(converter = com.handgrow.demo.util.StringListConverter.class)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private java.util.List<String> attachments;
}
