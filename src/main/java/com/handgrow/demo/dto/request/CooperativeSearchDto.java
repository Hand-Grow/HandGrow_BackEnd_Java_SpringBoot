package com.handgrow.demo.dto.request;

import com.handgrow.demo.entity.enums.Produce;
import lombok.Data;

@Data
public class CooperativeSearchDto {
    private String commune;
    private String province;
    private Produce produce;
}
