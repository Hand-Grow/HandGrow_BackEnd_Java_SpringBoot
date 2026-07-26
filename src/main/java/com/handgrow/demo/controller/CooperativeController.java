package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CooperativeSearchDto;
import com.handgrow.demo.dto.response.ApiResponse;
import com.handgrow.demo.dto.response.CooperativeResponse;
import com.handgrow.demo.entity.enums.Produce;
import com.handgrow.demo.service.CooperativeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cooperatives")
@RequiredArgsConstructor
public class CooperativeController {

    private final CooperativeService cooperativeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CooperativeResponse>>> getAllCooperatives() {
        List<CooperativeResponse> cooperatives = cooperativeService.getAllCooperatives();
        return ResponseEntity.ok(ApiResponse.success(cooperatives));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CooperativeResponse>>> searchCooperatives(
            @RequestParam(required = false) String commune,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) Produce produce) {

        CooperativeSearchDto searchDto = new CooperativeSearchDto();
        searchDto.setCommune(commune);
        searchDto.setProvince(province);
        searchDto.setProduce(produce);

        List<CooperativeResponse> cooperatives = cooperativeService.searchCooperatives(searchDto);
        return ResponseEntity.ok(ApiResponse.success(cooperatives));
    }
}
