package com.handgrow.demo.dto.request;

import lombok.Data;

@Data
public class EnterpriseRegisterRequest {
    private String companyName;
    private String username; // email
    private String phoneNumber;
    private String password;
    private String commune; // xã
    private String province; // tỉnh
}
