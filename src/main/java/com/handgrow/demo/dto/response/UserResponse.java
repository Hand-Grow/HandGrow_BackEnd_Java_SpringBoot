package com.handgrow.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String role;
    private String avatarUrl;
}
