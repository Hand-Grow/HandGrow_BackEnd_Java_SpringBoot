package com.handgrow.demo.entity;

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

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "commune")
    private String commune;

    @Column(name = "province")
    private String province;

    // Keep other fields for existing data
    @Column(name = "tax_code", length = 20)
    private String taxCode;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(length = 100)
    private String name;

    @Column(name = "representative_name")
    private String representativeName;
}
