package com.handgrow.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coop_announcements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoopAnnouncement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coop_id", nullable = false)
    private Cooperative cooperative;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "attachments", columnDefinition = "jsonb")
    private String attachments;
}
