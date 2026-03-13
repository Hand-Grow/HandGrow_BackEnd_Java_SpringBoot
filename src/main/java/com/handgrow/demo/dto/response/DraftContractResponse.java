package com.handgrow.demo.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftContractResponse {

    /** Metadata — giúp Frontend biết context của phòng chat */
    private String roomId;

    private UUID bulkSaleId;
    private UUID cooperativeId;
    private UUID enterpriseId;

    /** Các trường AI đọc được từ lịch sử chat (có thể null nếu AI chưa rõ) */
    private String productName;

    private String quantity;
    private String unitPrice;
    private String deliveryDate;
    private String deliveryLocation;

    /** true = Gemini đã trả lời; false = dùng fallback từ BulkSale */
    private boolean aiGenerated;
}
