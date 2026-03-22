package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateGroupBuyCampaignRequest;
import com.handgrow.demo.dto.request.JoinGroupBuyRequest;
import com.handgrow.demo.dto.response.GroupBuyCampaignResponse;
import com.handgrow.demo.dto.response.GroupBuyParticipationResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface GroupBuyService {
    GroupBuyCampaignResponse createCampaign(UUID coopAccountId, CreateGroupBuyCampaignRequest request);

    List<GroupBuyCampaignResponse> getCampaignsByCooperative(UUID coopAccountId, Pageable pageable);

    List<GroupBuyCampaignResponse> getAllGatheringCampaigns(Pageable pageable);

    GroupBuyCampaignResponse getCampaignById(UUID id);

    GroupBuyCampaignResponse joinCampaign(UUID campaignId, UUID farmerAccountId, JoinGroupBuyRequest request);

    GroupBuyCampaignResponse closeCampaign(UUID campaignId, UUID coopAccountId);

    List<GroupBuyParticipationResponse> getParticipationsByCampaign(UUID campaignId);

    Optional<GroupBuyParticipationResponse> getParticipationByCampaignAndFarmer(UUID campaignId, UUID farmerAccountId);
}
