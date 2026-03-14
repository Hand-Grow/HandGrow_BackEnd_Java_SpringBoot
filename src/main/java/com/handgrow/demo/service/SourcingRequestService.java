package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateSourcingRequest;
import com.handgrow.demo.dto.response.SourcingRequestResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.SourcingRequest;
import com.handgrow.demo.entity.SourcingRequest.SourcingRequestStatus;
import com.handgrow.demo.repository.AccountRepository;
import com.handgrow.demo.repository.SourcingRequestRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SourcingRequestService {

    private final SourcingRequestRepository sourcingRequestRepository;
    private final AccountRepository accountRepository;

    public SimpleResponse createSourcingRequest(CreateSourcingRequest request, UUID enterpriseId) {
        Account enterprise = accountRepository.findById(enterpriseId)
                .orElseThrow(() -> new RuntimeException("Enterprise not found"));

        SourcingRequest sourcingRequest = SourcingRequest.builder()
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .expectedPrice(request.getExpectedPrice())
                .deadline(request.getDeadline())
                .requirements(request.getRequirements())
                .enterprise(enterprise)
                .status(SourcingRequestStatus.OPEN)
                .build();

        SourcingRequest savedRequest = sourcingRequestRepository.save(sourcingRequest);

        return SimpleResponse.builder()
                .message("Sourcing request created successfully")
                .status(HttpStatus.CREATED.value())
                .success(true)
                .data(savedRequest.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<SourcingRequestResponse> getAllSourcingRequests(Pageable pageable) {
        return sourcingRequestRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<SourcingRequestResponse> getOpenSourcingRequests(Pageable pageable) {
        return sourcingRequestRepository.findByStatus(SourcingRequestStatus.OPEN, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<SourcingRequestResponse> searchSourcingRequests(String productName, String status, Pageable pageable) {
        SourcingRequestStatus requestStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                requestStatus = SourcingRequestStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + status);
            }
        }

        return sourcingRequestRepository.searchSourcingRequests(productName, requestStatus, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<SourcingRequestResponse> getMySourcingRequests(UUID enterpriseId, Pageable pageable) {
        return sourcingRequestRepository.findByEnterpriseId(enterpriseId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public SourcingRequestResponse getSourcingRequestById(UUID id) {
        SourcingRequest request = sourcingRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sourcing request not found"));
        return convertToResponse(request);
    }

    public SimpleResponse updateSourcingRequestStatus(UUID id, SourcingRequestStatus status) {
        SourcingRequest request = sourcingRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sourcing request not found"));

        request.setStatus(status);
        sourcingRequestRepository.save(request);

        return SimpleResponse.builder()
                .message("Sourcing request status updated successfully")
                .status(HttpStatus.OK.value())
                .success(true)
                .data(request.getId())
                .build();
    }

    public SimpleResponse cancelSourcingRequest(UUID id, UUID enterpriseId) {
        SourcingRequest request = sourcingRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sourcing request not found"));

        if (!request.getEnterprise().getId().equals(enterpriseId)) {
            throw new RuntimeException("You are not authorized to cancel this request");
        }

        if (request.getStatus() != SourcingRequestStatus.OPEN) {
            throw new RuntimeException("Only open requests can be cancelled");
        }

        request.setStatus(SourcingRequestStatus.CANCELLED);
        sourcingRequestRepository.save(request);

        return SimpleResponse.builder()
                .message("Sourcing request cancelled successfully")
                .status(HttpStatus.OK.value())
                .success(true)
                .data(request.getId())
                .build();
    }

    private SourcingRequestResponse convertToResponse(SourcingRequest request) {
        return SourcingRequestResponse.builder()
                .id(request.getId())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .expectedPrice(request.getExpectedPrice())
                .deadline(request.getDeadline())
                .requirements(request.getRequirements())
                .status(request.getStatus().name())
                .enterpriseId(request.getEnterprise().getId())
                .enterpriseName(request.getEnterprise().getUsername())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
