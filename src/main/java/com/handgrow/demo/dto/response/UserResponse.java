package com.handgrow.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String fullName;
    private String username;
    private String phoneNumber;
    private String role;
    private String avatarUrl;
    private String address;
    private String commune;
    private String province;
    private String produce;
    private String cooperativeId; // ID của HTX mà farmer thuộc về
    private String cooperativeName; // Tên HTX
}
