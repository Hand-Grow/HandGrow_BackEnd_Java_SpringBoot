package com.handgrow.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestResponse {
    private String id;
    private String farmerName;
    private String farmerPhone;
    private String farmerAddress;
    private String cooperativeName;
    private String cooperativeId;
    private String status;
    private String responseMessage;
    private String createdAt;
}
