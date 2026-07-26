package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.response.CooperativeResponse;
import com.handgrow.demo.entity.Cooperative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface CooperativeMapper {

    @Mapping(target = "id", source = "cooperative.id")
    @Mapping(
            target = "memberCount",
            expression = "java(cooperative.getMembers() != null ? cooperative.getMembers().size() : 0)")
    CooperativeResponse toResponse(Cooperative cooperative);
}
