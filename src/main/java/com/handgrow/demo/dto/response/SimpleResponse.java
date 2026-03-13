package com.handgrow.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponse {
    private int status;
    private String message;
    private boolean success;
    private Object data;
    
    public SimpleResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}   
