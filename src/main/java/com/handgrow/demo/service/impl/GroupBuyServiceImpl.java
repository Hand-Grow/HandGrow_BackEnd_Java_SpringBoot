package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CreateGroupBuyCampaignRequest;
import com.handgrow.demo.dto.request.JoinGroupBuyRequest;
import com.handgrow.demo.dto.response.GroupBuyCampaignResponse;
import com.handgrow.demo.dto.response.GroupBuyParticipationResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.entity.enums.GroupBuyCampaignStatus;
import com.handgrow.demo.repository.*;
import com.handgrow.demo.service.GroupBuyService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupBuyServiceImpl implements GroupBuyService {

    private final GroupBuyCampaignRepository campaignRepository;
    private final GroupBuyParticipationRepository participationRepository;
    private final ProductRepository productRepository;
    private final CooperativeRepository cooperativeRepository;
    private final FarmerRepository farmerRepository;
    private final GroupBuyOrderRepository orderRepository;

    @Override
    @Transactional
    public GroupBuyCampaignResponse createCampaign(UUID coopAccountId, CreateGroupBuyCampaignRequest request) {
        Cooperative coop = cooperativeRepository
                .findByAccountId(coopAccountId)
                .orElseThrow(() -> new RuntimeException("Cooperative not found"));

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        GroupBuyCampaign campaign = GroupBuyCampaign.builder()
                .cooperative(coop)
                .product(product)
                .title(request.getTitle())
                .description(request.getDescription())
                .deadlineDate(request.getDeadlineDate())
                .currentUnitPrice(product.getBasePrice())
                .totalCommittedQty(BigDecimal.ZERO)
                .status(GroupBuyCampaignStatus.GATHERING)
                .build();

        return mapToResponse(campaignRepository.save(campaign));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupBuyCampaignResponse> getCampaignsByCooperative(UUID coopAccountId, Pageable pageable) {
        Cooperative coop = cooperativeRepository
                .findByAccountId(coopAccountId)
                .orElseThrow(() -> new RuntimeException("Cooperative not found"));
        return campaignRepository.findByCooperativeId(coop.getId(), pageable).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupBuyCampaignResponse> getAllGatheringCampaigns(Pageable pageable) {
        return campaignRepository.findByStatus(GroupBuyCampaignStatus.GATHERING, pageable).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GroupBuyCampaignResponse getCampaignById(UUID id) {
        GroupBuyCampaign campaign =
                campaignRepository.findById(id).orElseThrow(() -> new RuntimeException("Campaign not found"));
        return mapToResponse(campaign);
    }

    @Override
    @Transactional
    public GroupBuyCampaignResponse joinCampaign(UUID campaignId, UUID farmerAccountId, JoinGroupBuyRequest request) {
        GroupBuyCampaign campaign =
                campaignRepository.findById(campaignId).orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (campaign.getStatus() != GroupBuyCampaignStatus.GATHERING) {
            throw new RuntimeException("Campaign is not accepting participations");
        }

        Farmer farmer = farmerRepository
                .findByAccountId(farmerAccountId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        GroupBuyParticipation participation = participationRepository
                .findByCampaignIdAndFarmerId(campaignId, farmer.getId())
                .orElse(GroupBuyParticipation.builder()
                        .campaign(campaign)
                        .farmer(farmer)
                        .committedQty(BigDecimal.ZERO)
                        .build());

        participation.setCommittedQty(request.getCommittedQty());
        participationRepository.save(participation);

        // Recalculate total qty and update campaign price
        updateCampaignStatus(campaign);

        return mapToResponse(campaign);
    }

    @Override
    @Transactional
    public GroupBuyCampaignResponse closeCampaign(UUID campaignId, UUID coopAccountId) {
        GroupBuyCampaign campaign =
                campaignRepository.findById(campaignId).orElseThrow(() -> new RuntimeException("Campaign not found"));

        Cooperative coop = cooperativeRepository
                .findByAccountId(coopAccountId)
                .orElseThrow(() -> new RuntimeException("Cooperative not found"));

        if (!campaign.getCooperative().getId().equals(coop.getId())) {
            throw new RuntimeException("Not authorized to close this campaign");
        }

        if (campaign.getStatus() != GroupBuyCampaignStatus.GATHERING) {
            throw new RuntimeException("Campaign is already closed");
        }

        campaign.setStatus(GroupBuyCampaignStatus.CLOSED);

        // Create consolidated order
        GroupBuyOrder order = GroupBuyOrder.builder()
                .campaign(campaign)
                .enterprise(campaign.getProduct().getEnterprise())
                .totalQty(campaign.getTotalCommittedQty())
                .finalUnitPrice(campaign.getCurrentUnitPrice())
                .totalAmount(campaign.getTotalCommittedQty().multiply(campaign.getCurrentUnitPrice()))
                .build();

        GroupBuyOrder savedOrder = orderRepository.save(order);
        campaign.setFinalOrder(savedOrder);

        // Lock prices for all participations
        List<GroupBuyParticipation> participations = participationRepository.findByCampaignId(campaignId);
        participations.forEach(p -> {
            p.setLockedUnitPrice(campaign.getCurrentUnitPrice());
            p.setTotalAmount(p.getCommittedQty().multiply(campaign.getCurrentUnitPrice()));
        });
        participationRepository.saveAll(participations);

        return mapToResponse(campaignRepository.save(campaign));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupBuyParticipationResponse> getParticipationsByCampaign(UUID campaignId) {
        return participationRepository.findByCampaignId(campaignId).stream()
                .map(p -> GroupBuyParticipationResponse.builder()
                        .id(p.getId())
                        .farmerId(p.getFarmer().getId())
                        .farmerName(p.getFarmer().getFullName())
                        .committedQty(p.getCommittedQty())
                        .lockedUnitPrice(p.getLockedUnitPrice())
                        .totalAmount(p.getTotalAmount())
                        .build())
                .collect(Collectors.toList());
    }

    private void updateCampaignStatus(GroupBuyCampaign campaign) {
        BigDecimal totalQty = participationRepository.sumCommittedQtyByCampaignId(campaign.getId());
        if (totalQty == null) totalQty = BigDecimal.ZERO;
        campaign.setTotalCommittedQty(totalQty);

        // Tier pricing logic
        Product product = campaign.getProduct();
        List<Product.PriceTier> tiers = product.getPriceTiers();

        BigDecimal newPrice = product.getBasePrice();
        if (tiers != null && !tiers.isEmpty()) {
            // Sort tiers by minQty descending to find the highest applicable tier
            List<Product.PriceTier> sortedTiers = tiers.stream()
                    .sorted(Comparator.comparing(Product.PriceTier::getMinQty).reversed())
                    .collect(Collectors.toList());

            for (Product.PriceTier tier : sortedTiers) {
                if (totalQty.compareTo(new BigDecimal(tier.getMinQty())) >= 0) {
                    newPrice = tier.getPrice();
                    break;
                }
            }
        }
        campaign.setCurrentUnitPrice(newPrice);
        campaignRepository.save(campaign);
    }

    private GroupBuyCampaignResponse mapToResponse(GroupBuyCampaign campaign) {
        BigDecimal totalQty = campaign.getTotalCommittedQty();
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

        return GroupBuyCampaignResponse.builder()
                .id(campaign.getId())
                .cooperativeId(campaign.getCooperative().getId())
                .cooperativeName(campaign.getCooperative().getName())
                .productId(campaign.getProduct().getId())
                .productName(campaign.getProduct().getName())
                .productImageUrl(campaign.getProduct().getImageUrl())
                .title(campaign.getTitle())
                .description(campaign.getDescription())
                .status(campaign.getStatus())
                .deadlineDate(campaign.getDeadlineDate())
                .totalCommittedQty(campaign.getTotalCommittedQty())
                .currentUnitPrice(campaign.getCurrentUnitPrice())
                .participationCount(participationRepository
                        .findByCampaignId(campaign.getId())
                        .size())
                .progressPercent(progressPercent)
                .nextTierLabel(nextTierLabel)
                .build();
    }
}
