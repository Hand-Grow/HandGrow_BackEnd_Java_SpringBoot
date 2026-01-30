package com.handgrow.demo.dto.request;

import lombok.Data;

@Data
public class FarmerRegisterRequest {
    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
}
