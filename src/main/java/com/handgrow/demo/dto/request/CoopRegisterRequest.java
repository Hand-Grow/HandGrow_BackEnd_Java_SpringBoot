package com.handgrow.demo.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CoopRegisterRequest {
    private String username;
    private String password;
    private String name;
    private String address;
    private String representativeName;

}