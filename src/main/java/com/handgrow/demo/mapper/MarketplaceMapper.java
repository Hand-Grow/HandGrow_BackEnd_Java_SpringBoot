package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.response.BulkSaleResponse;
import com.handgrow.demo.dto.response.CommitmentResponse;
import com.handgrow.demo.dto.response.OfferResponse;
import com.handgrow.demo.entity.BulkSale;
import com.handgrow.demo.entity.CollectionCommitment;
import com.handgrow.demo.entity.SaleOffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface MarketplaceMapper {

    @Mapping(target = "campaignId", source = "campaign.id")
    @Mapping(target = "coopName", source = "cooperative.name")
    BulkSaleResponse toBulkSaleResponse(BulkSale entity);

    @Mapping(target = "farmerName", source = "farmer.fullName")
    @Mapping(target = "plotName", source = "plot.name")
    @Mapping(target = "quantity", source = "committedQuantity")
    CommitmentResponse toCommitmentResponse(CollectionCommitment entity);

    @Mapping(target = "enterpriseName", source = "enterprise.name")
    OfferResponse toOfferResponse(SaleOffer entity);
}
