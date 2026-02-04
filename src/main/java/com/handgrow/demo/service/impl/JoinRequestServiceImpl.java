package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.JoinRequestDto;
import com.handgrow.demo.dto.request.JoinRequestResponseDto;
import com.handgrow.demo.dto.response.JoinRequestResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.entity.enums.JoinRequestStatus;
import com.handgrow.demo.repository.*;
import com.handgrow.demo.service.JoinRequestService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JoinRequestServiceImpl implements JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final AccountRepository accountRepository;
    private final FarmerRepository farmerRepository;
    private final CooperativeRepository cooperativeRepository;

    @Override
    @Transactional
    public JoinRequestResponse createJoinRequest(String farmerUsername, JoinRequestDto request) {
        Account farmerAccount = accountRepository
                .findByUsername(farmerUsername)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Farmer farmer = farmerRepository
                .findByAccount(farmerAccount)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found"));

        Cooperative cooperative = cooperativeRepository
                .findById(request.getCooperativeId())
                .orElseThrow(() -> new RuntimeException("Cooperative not found"));

        // Check if farmer already has pending request
        if (joinRequestRepository.existsByFarmerAndCooperativeAndStatus(
                farmer, cooperative, JoinRequestStatus.PENDING)) {
            throw new RuntimeException("You already have a pending request to this cooperative");
        }

        // Check if farmer is already member
        if (farmer.getCooperative() != null && farmer.getCooperative().equals(cooperative)) {
            throw new RuntimeException("You are already a member of this cooperative");
        }

        JoinRequest joinRequest = JoinRequest.builder()
                .farmer(farmer)
                .cooperative(cooperative)
                .status(JoinRequestStatus.PENDING)
                .build();

        joinRequest = joinRequestRepository.save(joinRequest);

        return buildJoinRequestResponse(joinRequest);
    }

    @Override
    public List<JoinRequestResponse> getPendingRequests(String coopUsername) {
        Account coopAccount = accountRepository
                .findByUsername(coopUsername)
                .orElseThrow(() -> new RuntimeException("Cooperative not found"));

        Cooperative cooperative = cooperativeRepository
                .findByAccount(coopAccount)
                .orElseThrow(() -> new RuntimeException("Cooperative profile not found"));

        List<JoinRequest> pendingRequests =
                joinRequestRepository.findByCooperativeAndStatus(cooperative, JoinRequestStatus.PENDING);

        return pendingRequests.stream().map(this::buildJoinRequestResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JoinRequestResponse respondToRequest(String coopUsername, UUID requestId, JoinRequestResponseDto response) {
        Account coopAccount = accountRepository
                .findByUsername(coopUsername)
                .orElseThrow(() -> new RuntimeException("Cooperative not found"));

        Cooperative cooperative = cooperativeRepository
                .findByAccount(coopAccount)
                .orElseThrow(() -> new RuntimeException("Cooperative profile not found"));

        JoinRequest joinRequest = joinRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Join request not found"));

        // Verify request belongs to this cooperative
        if (!joinRequest.getCooperative().equals(cooperative)) {
            throw new RuntimeException("You can only respond to requests for your cooperative");
        }

        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }

        // Update request status
        joinRequest.setStatus(response.isApproved() ? JoinRequestStatus.APPROVED : JoinRequestStatus.REJECTED);
        joinRequest.setResponseMessage(response.getResponseMessage());

        // If approved, add farmer to cooperative
        if (response.isApproved()) {
            Farmer farmer = joinRequest.getFarmer();
            farmer.setCooperative(cooperative);
            farmerRepository.save(farmer);
        }

        joinRequest = joinRequestRepository.save(joinRequest);

        return buildJoinRequestResponse(joinRequest);
    }

    @Override
    public List<JoinRequestResponse> getFarmerRequests(String farmerUsername) {
        Account farmerAccount = accountRepository
                .findByUsername(farmerUsername)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Farmer farmer = farmerRepository
                .findByAccount(farmerAccount)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found"));

        List<JoinRequest> requests = joinRequestRepository.findByFarmerAndStatus(farmer, JoinRequestStatus.PENDING);

        return requests.stream().map(this::buildJoinRequestResponse).collect(Collectors.toList());
    }

    private JoinRequestResponse buildJoinRequestResponse(JoinRequest joinRequest) {
        return JoinRequestResponse.builder()
                .id(joinRequest.getId().toString())
                .farmerName(joinRequest.getFarmer().getFullName())
                .farmerPhone(joinRequest.getFarmer().getPhoneNumber())
                .cooperativeName(joinRequest.getCooperative().getName())
                .status(joinRequest.getStatus().name())
                .responseMessage(joinRequest.getResponseMessage())
                .createdAt(joinRequest.getCreatedAt().toString())
                .build();
    }
}
