package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.JoinRequestDto;
import com.handgrow.demo.dto.request.JoinRequestResponseDto;
import com.handgrow.demo.dto.response.JoinRequestResponse;
import com.handgrow.demo.entity.enums.JoinRequestStatus;
import java.util.List;
import java.util.UUID;

public interface JoinRequestService {

    JoinRequestResponse createJoinRequest(String farmerUsername, JoinRequestDto request);

    List<JoinRequestResponse> getRequestsByStatus(String coopUsername, JoinRequestStatus status);

    JoinRequestResponse respondToRequest(String coopUsername, UUID requestId, JoinRequestResponseDto response);

    List<JoinRequestResponse> getFarmerRequests(String farmerUsername);

    List<JoinRequestResponse> getFarmerRequestsByStatus(String farmerUsername, JoinRequestStatus status);
}
