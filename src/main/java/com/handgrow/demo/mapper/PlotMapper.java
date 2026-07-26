package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.request.CreatePlotRequest;
import com.handgrow.demo.dto.response.PlotResponse;
import com.handgrow.demo.entity.Plot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface PlotMapper {
    PlotResponse toResponse(Plot plot);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farmer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Plot toEntity(CreatePlotRequest request);
}
