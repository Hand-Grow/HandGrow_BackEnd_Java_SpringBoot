package com.handgrow.demo.document;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MongoChatMessage {

    @Id
    private String id;

    @Indexed
    @Field("room_id")
    private String roomId;

    @Field("sender_id")
    private UUID senderId;

    @Field("sender_type")
    private String senderType; // E.g., "ENTERPRISE", "COOPERATIVE"

    @Field("sender_name")
    private String senderName;

    @Field("content")
    private String content;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}
