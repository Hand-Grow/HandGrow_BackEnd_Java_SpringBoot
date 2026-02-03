package com.handgrow.demo.dto.request;

import com.handgrow.demo.entity.enums.Produce;
import lombok.Data;

@Data
public class FarmerRegisterRequest {
    private String fullName;
    private String username; // email
    private String phoneNumber;
    private String password;
    private Produce produce;
}
