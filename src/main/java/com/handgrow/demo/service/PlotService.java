package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreatePlotRequest;
import com.handgrow.demo.dto.response.PlotResponse;
import java.util.List;

public interface PlotService {
    List<PlotResponse> getMyPlots(String username);

    PlotResponse createPlot(String username, CreatePlotRequest request);
}
