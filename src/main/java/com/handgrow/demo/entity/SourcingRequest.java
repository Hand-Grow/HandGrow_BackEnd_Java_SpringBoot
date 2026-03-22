package com.handgrow.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "sourcing_requests")
public class SourcingRequest extends BaseEntity {

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "expected_price")
    private BigDecimal expectedPrice;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id", nullable = false)
    private Account enterprise;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SourcingRequestStatus status = SourcingRequestStatus.OPEN;

    public enum SourcingRequestStatus {
        OPEN,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
