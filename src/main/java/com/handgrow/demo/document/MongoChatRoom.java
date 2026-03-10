package com.handgrow.demo.document;

import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MongoChatRoom {

    @Id
    private String id;

    @Indexed
    private String bulkSaleId;

    private String productName;

    @Indexed
    private String cooperativeId;

    private String cooperativeName;

    @Indexed
    private String enterpriseId;

    private String enterpriseName;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, CLOSED

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
