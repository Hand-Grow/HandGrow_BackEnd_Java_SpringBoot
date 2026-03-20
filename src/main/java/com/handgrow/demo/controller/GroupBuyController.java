package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateGroupBuyCampaignRequest;
import com.handgrow.demo.dto.request.JoinGroupBuyRequest;
import com.handgrow.demo.dto.response.GroupBuyCampaignResponse;
import com.handgrow.demo.dto.response.GroupBuyParticipationResponse;
import com.handgrow.demo.repository.AccountRepository;
import com.handgrow.demo.service.GroupBuyService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group-buy")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;
    private final AccountRepository accountRepository;

    private UUID getAccountId(Principal principal) {
        return accountRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"))
                .getId();
    }

    @PostMapping("/campaigns")
    public ResponseEntity<GroupBuyCampaignResponse> createCampaign(
            @RequestBody CreateGroupBuyCampaignRequest request, Principal principal) {
        return ResponseEntity.ok(groupBuyService.createCampaign(getAccountId(principal), request));
    }

    @GetMapping("/campaigns")
    public ResponseEntity<List<GroupBuyCampaignResponse>> getAllCampaigns(Pageable pageable) {
        return ResponseEntity.ok(groupBuyService.getAllGatheringCampaigns(pageable));
    }

    @GetMapping("/campaigns/cooperative/me")
    public ResponseEntity<List<GroupBuyCampaignResponse>> getMyCampaigns(Principal principal, Pageable pageable) {
        return ResponseEntity.ok(groupBuyService.getCampaignsByCooperative(getAccountId(principal), pageable));
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<GroupBuyCampaignResponse> getCampaignById(@PathVariable UUID id) {
        return ResponseEntity.ok(groupBuyService.getCampaignById(id));
    }

    @PostMapping("/campaigns/{id}/join")
    public ResponseEntity<GroupBuyCampaignResponse> joinCampaign(
            @PathVariable UUID id, @RequestBody JoinGroupBuyRequest request, Principal principal) {
        return ResponseEntity.ok(groupBuyService.joinCampaign(id, getAccountId(principal), request));
    }

    @PostMapping("/campaigns/{id}/close")
    public ResponseEntity<GroupBuyCampaignResponse> closeCampaign(@PathVariable UUID id, Principal principal) {
        return ResponseEntity.ok(groupBuyService.closeCampaign(id, getAccountId(principal)));
    }

    @GetMapping("/campaigns/{id}/participations")
    public ResponseEntity<List<GroupBuyParticipationResponse>> getParticipations(@PathVariable UUID id) {
        return ResponseEntity.ok(groupBuyService.getParticipationsByCampaign(id));
    }
}
