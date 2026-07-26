package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.request.CreateSourcingRequest;
import com.handgrow.demo.dto.response.SourcingRequestResponse;
import com.handgrow.demo.entity.SourcingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SourcingRequestMapper {

    @Mapping(target = "enterpriseId", source = "enterprise.id")
    @Mapping(target = "enterpriseName", source = "enterprise.username")
    SourcingRequestResponse toResponse(SourcingRequest request);

    @Mapping(target = "enterprise", ignore = true)
    @Mapping(target = "status", ignore = true)
    SourcingRequest toEntity(CreateSourcingRequest request);
}
