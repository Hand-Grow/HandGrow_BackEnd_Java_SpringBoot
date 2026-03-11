package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateOfferRequest;
import com.handgrow.demo.dto.response.BulkSaleResponse;
import com.handgrow.demo.dto.response.CommitmentResponse;
import com.handgrow.demo.dto.response.OfferResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.entity.enums.OfferStatus;
import com.handgrow.demo.repository.AccountRepository;
import com.handgrow.demo.service.MarketplaceService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;
    private final AccountRepository accountRepository;

    private UUID getAccountId(Principal principal) {
        return accountRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"))
                .getId();
    }

    @GetMapping("/bulk-sales")
    public ResponseEntity<List<BulkSaleResponse>> searchBulkSales(Pageable pageable) {
        return ResponseEntity.ok(marketplaceService.searchBulkSales(pageable));
    }

    @GetMapping("/bulk-sales/{id}")
    public ResponseEntity<BulkSaleResponse> getBulkSaleDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(marketplaceService.getBulkSaleDetail(id));
    }

    @GetMapping("/bulk-sales/{id}/commitments")
    public ResponseEntity<List<CommitmentResponse>> getCommitmentsByBulkSale(@PathVariable UUID id, Pageable pageable) {
        return ResponseEntity.ok(marketplaceService.getCommitmentsByBulkSaleId(id, pageable));
    }

    @PostMapping("/bulk-sales/{id}/offers")
    public ResponseEntity<SimpleResponse> createOffer(
            @PathVariable UUID id, @RequestBody CreateOfferRequest request, Principal principal) {
        UUID enterpriseId = getAccountId(principal);
        return ResponseEntity.ok(marketplaceService.createOffer(id, enterpriseId, request));
    }

    @GetMapping("/enterprises/me/offers")
    public ResponseEntity<List<OfferResponse>> getMyOffers(
            @RequestParam(required = false) String status, Principal principal, Pageable pageable) {
        UUID enterpriseId = getAccountId(principal);
        OfferStatus offerStatus = status != null ? OfferStatus.valueOf(status.toUpperCase()) : OfferStatus.PENDING;
        return ResponseEntity.ok(marketplaceService.getMyOffers(enterpriseId, offerStatus, pageable));
    }
}
