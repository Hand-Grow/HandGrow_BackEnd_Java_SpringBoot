package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CreatePlotRequest;
import com.handgrow.demo.dto.response.PlotResponse;
import com.handgrow.demo.entity.Farmer;
import com.handgrow.demo.entity.Plot;
import com.handgrow.demo.repository.FarmerRepository;
import com.handgrow.demo.repository.PlotRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlotService {

    private final PlotRepository plotRepository;
    private final FarmerRepository farmerRepository;

    public List<PlotResponse> getMyPlots(String username) {
        Farmer farmer =
                farmerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Farmer not found"));

        return plotRepository.findByFarmerId(farmer.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlotResponse createPlot(String username, CreatePlotRequest request) {
        Farmer farmer =
                farmerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Farmer not found"));

        Plot plot = plotRepository.save(Plot.builder()
                .farmer(farmer)
                .name(request.getName())
                .location(request.getLocation())
                .area(request.getArea())
                .areaUnit(request.getAreaUnit())
                .build());

        return mapToResponse(plot);
    }

    private PlotResponse mapToResponse(Plot plot) {
        return PlotResponse.builder()
                .id(plot.getId())
                .name(plot.getName())
                .location(plot.getLocation())
                .area(plot.getArea())
                .areaUnit(plot.getAreaUnit())
                .createdAt(plot.getCreatedAt())
                .updatedAt(plot.getUpdatedAt())
                .build();
    }
}
