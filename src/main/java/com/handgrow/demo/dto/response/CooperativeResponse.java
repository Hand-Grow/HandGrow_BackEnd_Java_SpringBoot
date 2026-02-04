package com.handgrow.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CooperativeResponse {
    private String id;
    private String name;
    private String phoneNumber;
    private String commune;
    private String province;
    private String produce;
    private Integer memberCount;
}
