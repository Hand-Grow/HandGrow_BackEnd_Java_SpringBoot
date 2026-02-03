package com.handgrow.demo.entity;

import com.handgrow.demo.entity.enums.Produce;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "farmers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farmer extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false, unique = true)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id")
    private Cooperative cooperative;

    @Column(name = "full_name", nullable = false)
    private String fullName;

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

    @Column(name = "voice_profile_data", columnDefinition = "TEXT")
    private String voiceProfileData;

    @Column(name = "avatar_url")
    private String avatarUrl;
}
