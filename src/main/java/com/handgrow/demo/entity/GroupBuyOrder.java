package com.handgrow.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "group_buy_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBuyOrder extends BaseEntity {

    @OneToOne(mappedBy = "finalOrder", fetch = FetchType.LAZY)
    private GroupBuyCampaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id", nullable = false)
    private Enterprise enterprise;

    @Column(name = "total_qty", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalQty;

    @Column(name = "final_unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalUnitPrice;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "ordered_at")
    @Builder.Default
    private LocalDateTime orderedAt = LocalDateTime.now();
}
