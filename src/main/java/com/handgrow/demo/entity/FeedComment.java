package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.FeedTargetType;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "feed_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private FeedTargetType targetType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
