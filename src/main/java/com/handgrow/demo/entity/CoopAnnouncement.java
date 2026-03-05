package com.handgrow.demo.entity;

import com.handgrow.demo.util.StringListConverter;
import jakarta.persistence.*;
import java.util.List;
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
    @Convert(converter = StringListConverter.class)
    private List<String> attachments; // Array of image URLs
}
