package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateSourcingRequest;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.dto.response.SourcingRequestResponse;
import com.handgrow.demo.entity.SourcingRequest.SourcingRequestStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SourcingRequestService {
    SimpleResponse createSourcingRequest(CreateSourcingRequest request, UUID enterpriseId);

    Page<SourcingRequestResponse> getAllSourcingRequests(Pageable pageable);

    Page<SourcingRequestResponse> getOpenSourcingRequests(Pageable pageable);

    Page<SourcingRequestResponse> searchSourcingRequests(String productName, String status, Pageable pageable);

    Page<SourcingRequestResponse> getMySourcingRequests(UUID enterpriseId, Pageable pageable);

    SourcingRequestResponse getSourcingRequestById(UUID id);

    SimpleResponse updateSourcingRequestStatus(UUID id, SourcingRequestStatus status);

    SimpleResponse cancelSourcingRequest(UUID id, UUID enterpriseId);
}
