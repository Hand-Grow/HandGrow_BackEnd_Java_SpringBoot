package com.handgrow.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "sender_id", nullable = false)
    private java.util.UUID senderId;

    @Column(name = "sender_type", nullable = false, length = 50)
    private String senderType; // E.g., "ENTERPRISE", "COOPERATIVE"

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
