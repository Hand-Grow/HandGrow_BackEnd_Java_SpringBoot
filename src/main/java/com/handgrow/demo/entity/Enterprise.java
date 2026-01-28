package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.EnterpriseType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enterprises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enterprise extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false, unique = true)
    private Account account;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "tax_code", nullable = false, length = 20, unique = true)
    private String taxCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private EnterpriseType businessType;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "enterprise_type", nullable = false)
    private EnterpriseType enterpriseType;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "representative_name")
    private String representativeName;
}
