package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.response.GroupBuyCampaignResponse;
import com.handgrow.demo.dto.response.GroupBuyParticipationResponse;
import com.handgrow.demo.entity.GroupBuyCampaign;
import com.handgrow.demo.entity.GroupBuyParticipation;
import com.handgrow.demo.entity.Product;
import com.handgrow.demo.repository.GroupBuyParticipationRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public abstract class GroupBuyMapper {

    @Autowired
    protected GroupBuyParticipationRepository participationRepository;

    @Mapping(target = "cooperativeId", source = "cooperative.id")
    @Mapping(target = "cooperativeName", source = "cooperative.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "participationCount", ignore = true)
    @Mapping(target = "progressPercent", ignore = true)
    @Mapping(target = "nextTierLabel", ignore = true)
    public abstract GroupBuyCampaignResponse toCampaignResponse(GroupBuyCampaign entity);

    @Mapping(target = "farmerId", source = "farmer.id")
    @Mapping(target = "farmerName", source = "farmer.fullName")
    public abstract GroupBuyParticipationResponse toParticipationResponse(GroupBuyParticipation entity);

    @AfterMapping
    protected void calculateTiersAndProgress(
            GroupBuyCampaign campaign,
            @MappingTarget GroupBuyCampaignResponse.GroupBuyCampaignResponseBuilder responseBuilder) {
        BigDecimal totalQty = campaign.getTotalCommittedQty();
        if (totalQty == null) totalQty = BigDecimal.ZERO;

        List<Product.PriceTier> tiers = campaign.getProduct().getPriceTiers();

        Double progressPercent = 0.0;
        String nextTierLabel = "Đã đạt giá sàn";

        if (tiers != null && !tiers.isEmpty()) {
            List<Product.PriceTier> sortedTiers = tiers.stream()
                    .sorted(Comparator.comparing(Product.PriceTier::getMinQty))
                    .collect(Collectors.toList());

            Product.PriceTier nextTier = null;
            Product.PriceTier currentTier = null;

            for (Product.PriceTier tier : sortedTiers) {
                if (totalQty.compareTo(new BigDecimal(tier.getMinQty())) < 0) {
                    nextTier = tier;
                    break;
                }
                currentTier = tier;
            }

            if (nextTier != null) {
                BigDecimal min = (currentTier != null) ? new BigDecimal(currentTier.getMinQty()) : BigDecimal.ZERO;
                BigDecimal max = new BigDecimal(nextTier.getMinQty());
                BigDecimal range = max.subtract(min);
                if (range.compareTo(BigDecimal.ZERO) > 0) {
                    progressPercent = totalQty.subtract(min)
                            .divide(range, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(100))
                            .doubleValue();
                }
                nextTierLabel = "Còn " + max.subtract(totalQty) + " bao nữa để giảm còn " + nextTier.getPrice() + "k";
            }
        }

        responseBuilder.progressPercent(progressPercent);
        responseBuilder.nextTierLabel(nextTierLabel);

        if (participationRepository != null && campaign.getId() != null) {
            responseBuilder.participationCount(
                    participationRepository.findByCampaignId(campaign.getId()).size());
        }
    }
}
