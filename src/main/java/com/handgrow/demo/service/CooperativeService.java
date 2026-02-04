package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CooperativeSearchDto;
import com.handgrow.demo.dto.response.CooperativeResponse;
import java.util.List;

public interface CooperativeService {

    List<CooperativeResponse> searchCooperatives(CooperativeSearchDto searchDto);

    List<CooperativeResponse> getAllCooperatives();
}
