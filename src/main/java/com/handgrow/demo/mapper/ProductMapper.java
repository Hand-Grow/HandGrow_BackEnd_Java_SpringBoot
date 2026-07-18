package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.request.CreateProductRequest;
import com.handgrow.demo.dto.response.ProductResponse;
import com.handgrow.demo.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "enterpriseId", source = "enterprise.id")
    @Mapping(target = "enterpriseName", source = "enterprise.companyName")
    ProductResponse toResponse(Product product);

    @Mapping(target = "enterprise", ignore = true)
    Product toEntity(CreateProductRequest request);
}
