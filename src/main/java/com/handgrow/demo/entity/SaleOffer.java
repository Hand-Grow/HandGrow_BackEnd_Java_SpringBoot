package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.OfferStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "sale_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleOffer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulk_sale_id", nullable = false)
    private BulkSale bulkSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id", nullable = false)
    private Enterprise enterprise;

    @Column(name = "offered_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal offeredPrice;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private OfferStatus status = OfferStatus.PENDING;
}
