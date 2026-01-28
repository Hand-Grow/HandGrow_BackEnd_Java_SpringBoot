package com.handgrow.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "cooperatives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cooperative extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false, unique = true)
    private Account account;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "representative_name")
    private String representativeName;

    @Column(name = "fund_balance", precision = 15, scale = 2)
    private BigDecimal fundBalance;

    @OneToMany(mappedBy = "cooperative", fetch = FetchType.LAZY)
    private List<Farmer> members;
}