package com.handgrow.demo.document;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    @Field("bulk_sale_id")
    private UUID bulkSaleId;

    @Field("product_name")
    private String productName;

    @Indexed
    @Field("coop_id")
    private UUID cooperativeId;

    @Field("coop_name")
    private String cooperativeName;

    @Indexed
    @Field("enterprise_id")
    private UUID enterpriseId;

    @Field("enterprise_name")
    private String enterpriseName;

    @Field("status")
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, CLOSED

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
