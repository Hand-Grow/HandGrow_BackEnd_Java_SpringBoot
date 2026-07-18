package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CreateSourcingRequest;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.dto.response.SourcingRequestResponse;
import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.SourcingRequest;
import com.handgrow.demo.entity.SourcingRequest.SourcingRequestStatus;
import com.handgrow.demo.exception.AppException;
import com.handgrow.demo.exception.ErrorCode;
import com.handgrow.demo.mapper.SourcingRequestMapper;
import com.handgrow.demo.repository.AccountRepository;
import com.handgrow.demo.repository.SourcingRequestRepository;
import com.handgrow.demo.service.SourcingRequestService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SourcingRequestServiceImpl implements SourcingRequestService {

    private final SourcingRequestRepository sourcingRequestRepository;
    private final AccountRepository accountRepository;
    private final SourcingRequestMapper sourcingRequestMapper;

    public SimpleResponse createSourcingRequest(CreateSourcingRequest request, UUID enterpriseId) {
        Account enterprise = accountRepository
                .findById(enterpriseId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        SourcingRequest sourcingRequest = sourcingRequestMapper.toEntity(request);
        sourcingRequest.setEnterprise(enterprise);
        sourcingRequest.setStatus(SourcingRequestStatus.OPEN);

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
        return sourcingRequestRepository.findAll(pageable).map(sourcingRequestMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SourcingRequestResponse> getOpenSourcingRequests(Pageable pageable) {
        return sourcingRequestRepository
                .findByStatus(SourcingRequestStatus.OPEN, pageable)
                .map(sourcingRequestMapper::toResponse);
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

        return sourcingRequestRepository
                .searchSourcingRequests(productName, requestStatus, pageable)
                .map(sourcingRequestMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SourcingRequestResponse> getMySourcingRequests(UUID enterpriseId, Pageable pageable) {
        return sourcingRequestRepository
                .findByEnterpriseId(enterpriseId, pageable)
                .map(sourcingRequestMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SourcingRequestResponse getSourcingRequestById(UUID id) {
        SourcingRequest request = sourcingRequestRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        return sourcingRequestMapper.toResponse(request);
    }

    public SimpleResponse updateSourcingRequestStatus(UUID id, SourcingRequestStatus status) {
        SourcingRequest request = sourcingRequestRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

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
        SourcingRequest request = sourcingRequestRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!request.getEnterprise().getId().equals(enterpriseId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (request.getStatus() != SourcingRequestStatus.OPEN) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
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
}
