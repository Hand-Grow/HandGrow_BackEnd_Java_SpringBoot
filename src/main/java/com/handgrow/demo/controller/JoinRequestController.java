package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.JoinRequestDto;
import com.handgrow.demo.dto.request.JoinRequestResponseDto;
import com.handgrow.demo.dto.response.JoinRequestResponse;
import com.handgrow.demo.service.JoinRequestService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/join-requests")
@RequiredArgsConstructor
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    // Farmer tạo request join vào cooperative
    @PostMapping
    public ResponseEntity<JoinRequestResponse> createJoinRequest(
            Authentication authentication, @RequestBody JoinRequestDto request) {
        String username = authentication.getName();
        JoinRequestResponse response = joinRequestService.createJoinRequest(username, request);
        return ResponseEntity.ok(response);
    }

    // Farmer xem các request của mình
    @GetMapping("/my-requests")
    public ResponseEntity<List<JoinRequestResponse>> getMyRequests(Authentication authentication) {
        String username = authentication.getName();
        List<JoinRequestResponse> requests = joinRequestService.getFarmerRequests(username);
        return ResponseEntity.ok(requests);
    }

    // Cooperative xem các pending requests
    @GetMapping("/pending")
    public ResponseEntity<List<JoinRequestResponse>> getPendingRequests(Authentication authentication) {
        String username = authentication.getName();
        List<JoinRequestResponse> requests = joinRequestService.getPendingRequests(username);
        return ResponseEntity.ok(requests);
    }

    // Cooperative approve/reject request
    @PutMapping("/{requestId}/respond")
    public ResponseEntity<JoinRequestResponse> respondToRequest(
            Authentication authentication, @PathVariable UUID requestId, @RequestBody JoinRequestResponseDto response) {
        String username = authentication.getName();
        JoinRequestResponse result = joinRequestService.respondToRequest(username, requestId, response);
        return ResponseEntity.ok(result);
    }
}
