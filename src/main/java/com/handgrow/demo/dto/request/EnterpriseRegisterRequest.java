package com.handgrow.demo.dto.request;

import com.handgrow.demo.entity.enums.EnterpriseType;
import lombok.Data;

import java.util.UUID;

@Data
public class EnterpriseRegisterRequest {
    private String username;
    private String password;
    private String companyName;
    private String taxCode;
    private EnterpriseType businessType;
    private String contactEmail;
    private String websiteUrl;
    private String address;
    private EnterpriseType enterpriseType;
    private String name;
    private String phoneNumber;
    private String representativeName;

}