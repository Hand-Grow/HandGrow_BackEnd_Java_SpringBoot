package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.response.ElectronicContractResponse;
import com.handgrow.demo.entity.BulkSale;
import com.handgrow.demo.entity.Cooperative;
import com.handgrow.demo.entity.ElectronicContract;
import com.handgrow.demo.entity.Enterprise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface ContractMapper {

    @Mapping(source = "c.id", target = "id")
    @Mapping(source = "c.roomId", target = "roomId")
    @Mapping(source = "bulkSale.id", target = "bulkSaleId")
    @Mapping(source = "bulkSale.productName", target = "productName")
    @Mapping(source = "cooperative.id", target = "cooperativeId")
    @Mapping(source = "cooperative.name", target = "cooperativeName")
    @Mapping(source = "cooperative.address", target = "cooperativeAddress")
    @Mapping(source = "cooperative.phoneNumber", target = "cooperativePhone")
    @Mapping(source = "cooperative.representativeName", target = "cooperativeRepresentative")
    @Mapping(source = "enterprise.id", target = "enterpriseId")
    @Mapping(
            source = "enterprise.companyName",
            target = "enterpriseName",
            defaultExpression = "java(enterprise.getName())")
    @Mapping(source = "enterprise.address", target = "enterpriseAddress")
    @Mapping(source = "enterprise.phoneNumber", target = "enterprisePhone")
    @Mapping(source = "enterprise.taxCode", target = "enterpriseTaxCode")
    @Mapping(source = "enterprise.representativeName", target = "enterpriseRepresentative")
    @Mapping(source = "c.enterpriseSignatoryName", target = "enterpriseSignatoryName")
    @Mapping(source = "c.enterpriseSigned", target = "enterpriseSigned")
    @Mapping(source = "c.enterpriseSignedAt", target = "enterpriseSignedAt")
    @Mapping(source = "c.agreedPrice", target = "agreedPrice")
    @Mapping(source = "c.agreedQuantity", target = "agreedQuantity")
    @Mapping(source = "c.deliveryDate", target = "deliveryDate")
    @Mapping(source = "c.terms", target = "terms")
    @Mapping(source = "c.documentUrl", target = "documentUrl")
    @Mapping(source = "c.status", target = "status")
    @Mapping(source = "c.createdAt", target = "createdAt")
    @Mapping(source = "c.updatedAt", target = "updatedAt")
    ElectronicContractResponse toContractResponse(
            ElectronicContract c, BulkSale bulkSale, Cooperative cooperative, Enterprise enterprise);
}
