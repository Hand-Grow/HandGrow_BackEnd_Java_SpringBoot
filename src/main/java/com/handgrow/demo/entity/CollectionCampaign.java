package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.CampaignStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "collection_campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionCampaign extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coop_id", nullable = false)
    private Cooperative cooperative;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "expected_date", nullable = false)
    private LocalDate expectedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private CampaignStatus status = CampaignStatus.GATHERING;
}
