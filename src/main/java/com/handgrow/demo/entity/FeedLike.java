package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.FeedTargetType;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "feed_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private FeedTargetType targetType;
}
