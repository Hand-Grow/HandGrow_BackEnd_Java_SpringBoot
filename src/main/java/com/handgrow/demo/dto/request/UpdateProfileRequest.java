package com.handgrow.demo.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    // Common
    private String fullName;
    private String phoneNumber;
    private String address;
    private String commune;
    private String province;
    private String avatarUrl; // for farmer

    // Enterprise / Cooperative specific
    private String companyName; // enterprise.companyName or cooperative.name
    private String representativeName; // cooperative.representativeName or enterprise.representativeName
    private String contactEmail; // enterprise contact email
}
