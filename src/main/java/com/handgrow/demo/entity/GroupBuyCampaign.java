package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.GroupBuyCampaignStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "group_buy_campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBuyCampaign extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GroupBuyCampaignStatus status = GroupBuyCampaignStatus.GATHERING;

    @Column(name = "deadline_date", nullable = false)
    private LocalDate deadlineDate;

    @Column(name = "total_committed_qty", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalCommittedQty = BigDecimal.ZERO;

    @Column(name = "current_unit_price", precision = 15, scale = 2)
    private BigDecimal currentUnitPrice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "final_order_id")
    private GroupBuyOrder finalOrder;
}
