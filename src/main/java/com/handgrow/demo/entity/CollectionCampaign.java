package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.CampaignStatus;
import com.handgrow.demo.util.StringListConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
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

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "attachments", columnDefinition = "jsonb")
    @Convert(converter = StringListConverter.class)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private List<String> attachments;

    @Column(name = "expected_date", nullable = false)
    private LocalDate expectedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private CampaignStatus status = CampaignStatus.GATHERING;
}
