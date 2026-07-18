package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreatePlotRequest;
import com.handgrow.demo.dto.response.PlotResponse;
import com.handgrow.demo.service.PlotService;
import io.swagger.v3.oas.annotations.Operation;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/plots")
@RequiredArgsConstructor
public class PlotController {

    private final PlotService plotService;

    @GetMapping
    @Operation(summary = "Get all plots for current farmer")
    public ResponseEntity<List<PlotResponse>> getMyPlots(Principal principal) {
        return ResponseEntity.ok(plotService.getMyPlots(principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Create a new plot")
    public ResponseEntity<PlotResponse> createPlot(Principal principal, @RequestBody CreatePlotRequest request) {
        return ResponseEntity.ok(plotService.createPlot(principal.getName(), request));
    }
}
