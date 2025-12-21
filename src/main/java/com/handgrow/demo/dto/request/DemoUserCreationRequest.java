package com.handgrow.demo.dto.request;

import lombok.Data;

@Data
public class DemoUserCreationRequest {
    private String username;
    private String email;
    private String password;
}
