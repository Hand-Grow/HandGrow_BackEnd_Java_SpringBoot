package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateAnnouncementRequest;
import com.handgrow.demo.dto.request.CreateCampaignRequest;
import com.handgrow.demo.dto.request.CreateCommitmentRequest;
import com.handgrow.demo.dto.request.UpdateBulkSaleRequest;
import com.handgrow.demo.dto.response.CommitmentResponse;
import com.handgrow.demo.dto.response.OfferResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.service.CampaignService;
import com.handgrow.demo.service.MarketplaceService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final MarketplaceService marketplaceService;

    @PostMapping("/coops/{coopId}/announcements")
    public ResponseEntity<SimpleResponse> createAnnouncement(
            @PathVariable UUID coopId, @RequestBody CreateAnnouncementRequest request) {
        return ResponseEntity.ok(campaignService.createAnnouncement(coopId, request));
    }

    @PostMapping("/coops/{coopId}/campaigns")
    public ResponseEntity<SimpleResponse> createCampaign(
            @PathVariable UUID coopId, @RequestBody CreateCampaignRequest request) {
        return ResponseEntity.ok(campaignService.createCampaign(coopId, request));
    }

    @PostMapping("/campaigns/{id}/commitments")
    public ResponseEntity<SimpleResponse> addCommitment(
            @PathVariable UUID id, @RequestBody CreateCommitmentRequest request, Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(campaignService.addCommitment(id, username, request));
    }

    @GetMapping("/campaigns/{id}/commitments")
    public ResponseEntity<List<CommitmentResponse>> getCommitments(
            @PathVariable UUID id, Principal principal, Pageable pageable) {
        return ResponseEntity.ok(campaignService.getCommitments(id, principal.getName(), pageable));
    }

    @PostMapping("/campaigns/{id}/publish-to-b2b")
    public ResponseEntity<SimpleResponse> publishToB2B(
            @PathVariable UUID id, @RequestBody com.handgrow.demo.dto.request.PublishToB2BRequest request) {
        return ResponseEntity.ok(campaignService.publishToB2B(id, request));
    }

    @PutMapping("/bulk-sales/{id}")
    public ResponseEntity<SimpleResponse> updateBulkSale(
            @PathVariable UUID id, @RequestBody UpdateBulkSaleRequest request) {
        return ResponseEntity.ok(marketplaceService.updateBulkSale(id, request));
    }

    @GetMapping("/bulk-sales/{id}/offers")
    public ResponseEntity<List<OfferResponse>> getOffers(@PathVariable UUID id) {
        return ResponseEntity.ok(marketplaceService.getOffers(id));
    }

    @PutMapping("/offers/{offerId}/accept")
    public ResponseEntity<SimpleResponse> acceptOffer(@PathVariable UUID offerId) {
        return ResponseEntity.ok(marketplaceService.acceptOffer(offerId));
    }

    @PutMapping("/offers/{offerId}/reject")
    public ResponseEntity<SimpleResponse> rejectOffer(@PathVariable UUID offerId) {
        return ResponseEntity.ok(marketplaceService.rejectOffer(offerId));
    }
}
