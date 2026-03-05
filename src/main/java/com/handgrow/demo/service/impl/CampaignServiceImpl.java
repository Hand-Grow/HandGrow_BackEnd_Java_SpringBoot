package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CreateAnnouncementRequest;
import com.handgrow.demo.dto.request.CreateCampaignRequest;
import com.handgrow.demo.dto.request.CreateCommitmentRequest;
import com.handgrow.demo.dto.response.CommitmentResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.entity.enums.BulkSaleStatus;
import com.handgrow.demo.entity.enums.CampaignStatus;
import com.handgrow.demo.repository.*;
import com.handgrow.demo.service.CampaignService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CoopAnnouncementRepository announcementRepository;
    private final CollectionCampaignRepository campaignRepository;
    private final CollectionCommitmentRepository commitmentRepository;
    private final BulkSaleRepository bulkSaleRepository;
    private final CooperativeRepository cooperativeRepository;
    private final FarmerRepository farmerRepository;
    private final PlotRepository plotRepository;

    @Transactional
    public SimpleResponse createAnnouncement(UUID coopId, CreateAnnouncementRequest request) {
        Cooperative coop =
                cooperativeRepository.findById(coopId).orElseThrow(() -> new RuntimeException("Cooperative not found"));

        announcementRepository.save(CoopAnnouncement.builder()
                .cooperative(coop)
                .title(request.getTitle())
                .content(request.getContent())
                .attachments(request.getAttachments())
                .build());

        return new SimpleResponse("Đã tạo thông báo", true);
    }

    @Transactional
    public SimpleResponse createCampaign(UUID coopId, CreateCampaignRequest request) {
        Cooperative coop =
                cooperativeRepository.findById(coopId).orElseThrow(() -> new RuntimeException("Cooperative not found"));

        campaignRepository.save(CollectionCampaign.builder()
                .cooperative(coop)
                .productName(request.getProductName())
                .expectedDate(request.getExpectedDate())
                .build());

        return new SimpleResponse("Đã tạo đợt thu gom", true);
    }

    @Transactional
    public SimpleResponse addCommitment(UUID campaignId, UUID farmerId, CreateCommitmentRequest request) {
        CollectionCampaign campaign =
                campaignRepository.findById(campaignId).orElseThrow(() -> new RuntimeException("Campaign not found"));

        Farmer farmer = farmerRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));

        Plot plot =
                plotRepository.findById(request.getPlotId()).orElseThrow(() -> new RuntimeException("Plot not found"));

        commitmentRepository.save(CollectionCommitment.builder()
                .campaign(campaign)
                .farmer(farmer)
                .plot(plot)
                .committedQuantity(request.getCommittedQuantity())
                .build());

        return new SimpleResponse("Đã cam kết sản lượng", true);
    }

    @Transactional
    public List<CommitmentResponse> getCommitments(UUID campaignId, Pageable pageable) {
        return commitmentRepository.findByCampaignId(campaignId, pageable).stream()
                .map(c -> CommitmentResponse.builder()
                        .id(c.getId())
                        .farmerName(c.getFarmer().getFullName())
                        .plotName(c.getPlot().getName())
                        .quantity(c.getCommittedQuantity())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public SimpleResponse publishToB2B(UUID campaignId) {
        CollectionCampaign campaign =
                campaignRepository.findById(campaignId).orElseThrow(() -> new RuntimeException("Campaign not found"));

        campaign.setStatus(CampaignStatus.CLOSED);
        campaignRepository.save(campaign);

        BigDecimal totalQuantity = commitmentRepository.sumQuantityByCampaign(campaignId);

        bulkSaleRepository.save(BulkSale.builder()
                .cooperative(campaign.getCooperative())
                .campaign(campaign)
                .productName(campaign.getProductName())
                .totalQuantity(totalQuantity)
                .status(BulkSaleStatus.OPEN)
                .build());

        return new SimpleResponse("Đã đóng đợt gom và tạo bài B2B", true);
    }
}
