package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CreateOfferRequest;
import com.handgrow.demo.dto.request.UpdateBulkSaleRequest;
import com.handgrow.demo.dto.response.BulkSaleResponse;
import com.handgrow.demo.dto.response.CommitmentResponse;
import com.handgrow.demo.dto.response.OfferResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.entity.BulkSale;
import com.handgrow.demo.entity.Enterprise;
import com.handgrow.demo.entity.SaleOffer;
import com.handgrow.demo.entity.enums.BulkSaleStatus;
import com.handgrow.demo.entity.enums.OfferStatus;
import com.handgrow.demo.repository.BulkSaleRepository;
import com.handgrow.demo.repository.CollectionCommitmentRepository;
import com.handgrow.demo.repository.EnterpriseRepository;
import com.handgrow.demo.repository.SaleOfferRepository;
import com.handgrow.demo.service.MarketplaceService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketplaceServiceImpl implements MarketplaceService {

    private final BulkSaleRepository bulkSaleRepository;
    private final SaleOfferRepository offerRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final CollectionCommitmentRepository commitmentRepository;

    @Transactional
    public List<BulkSaleResponse> searchBulkSales(Pageable pageable) {
        return bulkSaleRepository.findByStatus(BulkSaleStatus.OPEN, pageable).stream()
                .map(b -> BulkSaleResponse.builder()
                        .id(b.getId())
                        .campaignId(b.getCampaign() != null ? b.getCampaign().getId() : null)
                        .productName(b.getProductName())
                        .totalQuantity(b.getTotalQuantity())
                        .expectedPrice(b.getExpectedPrice())
                        .status(b.getStatus())
                        .coopName(b.getCooperative().getName())
                        .attachments(b.getAttachments())
                        .createdAt(b.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public BulkSaleResponse getBulkSaleDetail(UUID id) {
        BulkSale sale = bulkSaleRepository.findById(id).orElseThrow(() -> new RuntimeException("Bulk sale not found"));

        return BulkSaleResponse.builder()
                .id(sale.getId())
                .campaignId(sale.getCampaign() != null ? sale.getCampaign().getId() : null)
                .productName(sale.getProductName())
                .totalQuantity(sale.getTotalQuantity())
                .expectedPrice(sale.getExpectedPrice())
                .status(sale.getStatus())
                .coopName(sale.getCooperative().getName())
                .attachments(sale.getAttachments())
                .createdAt(sale.getCreatedAt())
                .build();
    }

    @Transactional
    public List<CommitmentResponse> getCommitmentsByBulkSaleId(UUID bulkSaleId, Pageable pageable) {
        BulkSale sale =
                bulkSaleRepository.findById(bulkSaleId).orElseThrow(() -> new RuntimeException("Bulk sale not found"));

        if (sale.getCampaign() == null) {
            return List.of();
        }

        return commitmentRepository.findByCampaignId(sale.getCampaign().getId(), pageable).stream()
                .map(c -> CommitmentResponse.builder()
                        .id(c.getId())
                        .farmerName(c.getFarmer().getFullName())
                        .plotName(c.getPlot().getName())
                        .quantity(c.getCommittedQuantity())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public SimpleResponse updateBulkSale(UUID id, UpdateBulkSaleRequest request) {
        BulkSale sale = bulkSaleRepository.findById(id).orElseThrow(() -> new RuntimeException("Bulk sale not found"));

        sale.setExpectedPrice(request.getExpectedPrice());
        sale.setAttachments(request.getAttachments());
        bulkSaleRepository.save(sale);

        return new SimpleResponse("Đã cập nhật thông tin", true);
    }

    @Transactional
    public SimpleResponse createOffer(UUID bulkSaleId, UUID enterpriseId, CreateOfferRequest request) {
        BulkSale sale =
                bulkSaleRepository.findById(bulkSaleId).orElseThrow(() -> new RuntimeException("Bulk sale not found"));

        Enterprise enterprise = enterpriseRepository
                .findById(enterpriseId)
                .orElseThrow(() -> new RuntimeException("Enterprise not found"));

        offerRepository.save(SaleOffer.builder()
                .bulkSale(sale)
                .enterprise(enterprise)
                .offeredPrice(request.getOfferedPrice())
                .message(request.getMessage())
                .build());

        return new SimpleResponse("Đã gửi báo giá", true);
    }

    @Transactional
    public List<OfferResponse> getOffers(UUID bulkSaleId) {
        return offerRepository.findByBulkSaleId(bulkSaleId).stream()
                .map(o -> OfferResponse.builder()
                        .id(o.getId())
                        .enterpriseName(o.getEnterprise().getName())
                        .offeredPrice(o.getOfferedPrice())
                        .message(o.getMessage())
                        .status(o.getStatus())
                        .createdAt(o.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public SimpleResponse acceptOffer(UUID offerId) {
        SaleOffer offer = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Offer not found"));

        offer.setStatus(OfferStatus.ACCEPTED);
        offerRepository.save(offer);

        BulkSale sale = offer.getBulkSale();
        sale.setStatus(BulkSaleStatus.SOLD);
        bulkSaleRepository.save(sale);

        List<SaleOffer> otherOffers = offerRepository.findByBulkSaleIdAndStatus(sale.getId(), OfferStatus.PENDING);
        otherOffers.forEach(o -> o.setStatus(OfferStatus.REJECTED));
        offerRepository.saveAll(otherOffers);

        return new SimpleResponse("Đã chấp nhận báo giá", true);
    }

    @Transactional
    public SimpleResponse rejectOffer(UUID offerId) {
        SaleOffer offer = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Offer not found"));

        offer.setStatus(OfferStatus.REJECTED);
        offerRepository.save(offer);

        return new SimpleResponse("Đã từ chối báo giá", true);
    }

    @Transactional
    public List<OfferResponse> getMyOffers(UUID enterpriseId, OfferStatus status, Pageable pageable) {
        return offerRepository.findByEnterpriseIdAndStatus(enterpriseId, status, pageable).stream()
                .map(o -> OfferResponse.builder()
                        .id(o.getId())
                        .enterpriseName(o.getEnterprise().getName())
                        .offeredPrice(o.getOfferedPrice())
                        .message(o.getMessage())
                        .status(o.getStatus())
                        .createdAt(o.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
