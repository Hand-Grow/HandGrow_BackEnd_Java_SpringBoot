package com.handgrow.demo.dto.response;

import com.handgrow.demo.entity.enums.ContractStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectronicContractResponse {
    private UUID id;
    private String roomId;
    private UUID bulkSaleId;
    private String productName;
    private UUID cooperativeId;
    private String cooperativeName;
    private String cooperativeAddress;
    private String cooperativePhone;
    private String cooperativeRepresentative;
    private UUID enterpriseId;
    private String enterpriseName;
    private String enterpriseAddress;
    private String enterprisePhone;
    private String enterpriseTaxCode;
    private String enterpriseRepresentative;
    private BigDecimal agreedPrice;
    private BigDecimal agreedQuantity;
    private LocalDate deliveryDate;
    private String terms;
    private String documentUrl;
    private ContractStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enterprise signature info
    private String enterpriseSignatoryName;
    private Boolean enterpriseSigned;
    private LocalDateTime enterpriseSignedAt;
}
