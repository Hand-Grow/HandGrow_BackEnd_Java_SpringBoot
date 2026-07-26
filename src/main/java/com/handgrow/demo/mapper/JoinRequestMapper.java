package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.response.JoinRequestResponse;
import com.handgrow.demo.entity.JoinRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface JoinRequestMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "farmer.fullName", target = "farmerName")
    @Mapping(source = "farmer.phoneNumber", target = "farmerPhone")
    @Mapping(source = "farmer.province", target = "farmerAddress")
    @Mapping(source = "cooperative.name", target = "cooperativeName")
    @Mapping(source = "cooperative.id", target = "cooperativeId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "responseMessage", target = "responseMessage")
    @Mapping(source = "createdAt", target = "createdAt")
    JoinRequestResponse toJoinRequestResponse(JoinRequest joinRequest);
}
