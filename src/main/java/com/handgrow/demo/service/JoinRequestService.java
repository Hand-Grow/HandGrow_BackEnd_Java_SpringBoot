package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.JoinRequestDto;
import com.handgrow.demo.dto.request.JoinRequestResponseDto;
import com.handgrow.demo.dto.response.JoinRequestResponse;
import java.util.List;
import java.util.UUID;

public interface JoinRequestService {

    JoinRequestResponse createJoinRequest(String farmerUsername, JoinRequestDto request);

    List<JoinRequestResponse> getPendingRequests(String coopUsername);

    JoinRequestResponse respondToRequest(String coopUsername, UUID requestId, JoinRequestResponseDto response);

    List<JoinRequestResponse> getFarmerRequests(String farmerUsername);
}
