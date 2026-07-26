package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.response.UserResponse;
import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.Cooperative;
import com.handgrow.demo.entity.Enterprise;
import com.handgrow.demo.entity.Farmer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "id", source = "farmer.id")
    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "role", constant = "FARMER")
    @Mapping(target = "cooperativeId", source = "farmer.cooperative.id")
    @Mapping(target = "cooperativeName", source = "farmer.cooperative.name")
    UserResponse toUserResponse(Account account, Farmer farmer);

    @Mapping(target = "id", source = "coop.id")
    @Mapping(target = "fullName", source = "coop.name")
    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "role", constant = "COOP")
    UserResponse toUserResponse(Account account, Cooperative coop);

    @Mapping(target = "id", source = "enterprise.id")
    @Mapping(target = "fullName", source = "enterprise.companyName")
    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "role", constant = "ENTERPRISE")
    UserResponse toUserResponse(Account account, Enterprise enterprise);
}
