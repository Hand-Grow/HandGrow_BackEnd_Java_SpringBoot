package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.Produce;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;

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

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "commune")
    private String commune;

    @Column(name = "province")
    private String province;

    @Enumerated(EnumType.STRING)
    @Column(name = "produce")
    private Produce produce;

    @Column(name = "representative_name")
    private String representativeName;

    @Column(name = "fund_balance", precision = 15, scale = 2)
    private BigDecimal fundBalance;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToMany(mappedBy = "cooperative", fetch = FetchType.LAZY)
    private List<Farmer> members;
}
