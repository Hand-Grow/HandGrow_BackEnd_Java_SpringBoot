package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateAnnouncementRequest;
import com.handgrow.demo.dto.request.CreateCampaignRequest;
import com.handgrow.demo.dto.request.CreateCommitmentRequest;
import com.handgrow.demo.dto.response.CommitmentResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface CampaignService {
    SimpleResponse createAnnouncement(UUID coopId, CreateAnnouncementRequest request);

    SimpleResponse createCampaign(UUID coopId, CreateCampaignRequest request);

    SimpleResponse addCommitment(UUID campaignId, String username, CreateCommitmentRequest request);

    List<CommitmentResponse> getCommitments(UUID campaignId, Pageable pageable);

    SimpleResponse publishToB2B(UUID campaignId);
}
