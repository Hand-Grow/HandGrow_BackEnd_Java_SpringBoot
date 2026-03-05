package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateOfferRequest;
import com.handgrow.demo.dto.request.UpdateBulkSaleRequest;
import com.handgrow.demo.dto.response.BulkSaleResponse;
import com.handgrow.demo.dto.response.OfferResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.entity.enums.OfferStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MarketplaceService {
    List<BulkSaleResponse> searchBulkSales(Pageable pageable);

    BulkSaleResponse getBulkSaleDetail(UUID id);

    SimpleResponse updateBulkSale(UUID id, UpdateBulkSaleRequest request);

    SimpleResponse createOffer(UUID bulkSaleId, UUID enterpriseId, CreateOfferRequest request);

    List<OfferResponse> getOffers(UUID bulkSaleId);

    SimpleResponse acceptOffer(UUID offerId);

    SimpleResponse rejectOffer(UUID offerId);

    List<OfferResponse> getMyOffers(UUID enterpriseId, OfferStatus status, Pageable pageable);
}
