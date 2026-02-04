package com.handgrow.demo.dto.request;

import lombok.Data;

@Data
public class JoinRequestResponseDto {
    private String responseMessage;
    private boolean approved; // true = approve, false = reject
}
