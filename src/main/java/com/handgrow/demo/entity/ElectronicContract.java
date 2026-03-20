package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.ContractStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "electronic_contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectronicContract extends BaseEntity {

    @Column(name = "room_id", nullable = false, unique = true)
    private String roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulk_sale_id", nullable = false)
    private BulkSale bulkSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coop_id", nullable = false)
    private Cooperative cooperative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id", nullable = false)
    private Enterprise enterprise;

    @Column(name = "agreed_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal agreedPrice;

    @Column(name = "agreed_quantity", nullable = false, precision = 15, scale = 2)
    private BigDecimal agreedQuantity;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(name = "terms", columnDefinition = "TEXT")
    private String terms;

    // Enterprise signature fields
    @Column(name = "enterprise_signatory_name", length = 255)
    private String enterpriseSignatoryName;

    @Column(name = "enterprise_signed")
    @ColumnDefault("false")
    private Boolean enterpriseSigned = false;

    @Column(name = "enterprise_signed_at")
    private LocalDateTime enterpriseSignedAt;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ContractStatus status = ContractStatus.DRAFT;
}
