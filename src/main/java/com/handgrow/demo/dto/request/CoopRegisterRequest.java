package com.handgrow.demo.dto.request;

import com.handgrow.demo.entity.enums.Produce;
import lombok.Data;

@Data
public class CoopRegisterRequest {
    private String name;
    private String username; // email
    private String phoneNumber;
    private String password;
    private String commune; // xã
    private String province; // tỉnh
    private Produce produce;
}
