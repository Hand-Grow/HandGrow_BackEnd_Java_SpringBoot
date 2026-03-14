package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateSourcingRequest;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.dto.response.SourcingRequestResponse;
import com.handgrow.demo.entity.SourcingRequest.SourcingRequestStatus;
import com.handgrow.demo.repository.AccountRepository;
import com.handgrow.demo.service.SourcingRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sourcing-requests")
@RequiredArgsConstructor
@Tag(name = "Sourcing Requests", description = "API for managing sourcing requests")
public class SourcingRequestController {

    private final SourcingRequestService sourcingRequestService;
    private final AccountRepository accountRepository;

    private UUID getAccountId(Principal principal) {
        return accountRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"))
                .getId();
    }

    @PostMapping
    @PreAuthorize("hasRole('ENTERPRISE')")
    @Operation(summary = "Create a new sourcing request", description = "Creates a new sourcing request for enterprises to find suppliers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sourcing request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only enterprises can create requests")
    })
    
    public ResponseEntity<SimpleResponse> createSourcingRequest(
            @Valid @RequestBody CreateSourcingRequest request,
            Principal principal) {
        UUID enterpriseId = getAccountId(principal);
        SimpleResponse response = sourcingRequestService.createSourcingRequest(request, enterpriseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all sourcing requests", description = "Retrieves all sourcing requests with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sourcing requests")
    })
    public ResponseEntity<Page<SourcingRequestResponse>> getAllSourcingRequests(Pageable pageable) {
        Page<SourcingRequestResponse> requests = sourcingRequestService.getAllSourcingRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/open")
    @Operation(summary = "Get open sourcing requests", description = "Retrieves all open sourcing requests with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved open sourcing requests")
    })
    public ResponseEntity<Page<SourcingRequestResponse>> getOpenSourcingRequests(Pageable pageable) {
        Page<SourcingRequestResponse> requests = sourcingRequestService.getOpenSourcingRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/search")
    @Operation(summary = "Search sourcing requests", description = "Search sourcing requests by product name and/or status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    })
    public ResponseEntity<Page<SourcingRequestResponse>> searchSourcingRequests(
            @Parameter(description = "Product name to search for") @RequestParam(required = false) String productName,
            @Parameter(description = "Status filter (OPEN, IN_PROGRESS, COMPLETED, CANCELLED)") @RequestParam(required = false) String status,
            @Parameter(description = "Sort parameter (format: field,direction)") @RequestParam(required = false) String sort,
            Pageable pageable) {
        // Handle sort parameter if provided
        if (sort != null && !sort.isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                Sort.by(Sort.Direction.fromString(sort.split(",")[1]), sort.split(",")[0]));
        }
        
        Page<SourcingRequestResponse> requests = sourcingRequestService.searchSourcingRequests(productName, status, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ENTERPRISE')")
    @Operation(summary = "Get my sourcing requests", description = "Retrieves sourcing requests created by the authenticated enterprise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved my sourcing requests"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only enterprises can access")
    })
    public ResponseEntity<Page<SourcingRequestResponse>> getMySourcingRequests(
            Principal principal,
            Pageable pageable) {
        UUID enterpriseId = getAccountId(principal);
        Page<SourcingRequestResponse> requests = sourcingRequestService.getMySourcingRequests(enterpriseId, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sourcing request by ID", description = "Retrieves a specific sourcing request by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sourcing request"),
            @ApiResponse(responseCode = "404", description = "Sourcing request not found")
    })
    public ResponseEntity<SourcingRequestResponse> getSourcingRequestById(
            @Parameter(description = "ID of the sourcing request to retrieve") @PathVariable UUID id) {
        SourcingRequestResponse request = sourcingRequestService.getSourcingRequestById(id);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update sourcing request status", description = "Updates the status of a sourcing request (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "404", description = "Sourcing request not found")
    })
    public ResponseEntity<SimpleResponse> updateSourcingRequestStatus(
            @Parameter(description = "ID of the sourcing request") @PathVariable UUID id,
            @Parameter(description = "New status") @RequestParam SourcingRequestStatus status) {
        SimpleResponse response = sourcingRequestService.updateSourcingRequestStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ENTERPRISE')")
    @Operation(summary = "Cancel sourcing request", description = "Cancels a sourcing request (only by the owner enterprise)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request cancelled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only owner can cancel"),
            @ApiResponse(responseCode = "404", description = "Sourcing request not found"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel request in current status")
    })
    public ResponseEntity<SimpleResponse> cancelSourcingRequest(
            @Parameter(description = "ID of the sourcing request to cancel") @PathVariable UUID id,
            Principal principal) {
        UUID enterpriseId = getAccountId(principal);
        SimpleResponse response = sourcingRequestService.cancelSourcingRequest(id, enterpriseId);
        return ResponseEntity.ok(response);
    }
}
