package com.handgrow.demo.repository;

import com.handgrow.demo.entity.SaleOffer;
import com.handgrow.demo.entity.enums.OfferStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleOfferRepository extends JpaRepository<SaleOffer, UUID> {
    List<SaleOffer> findByBulkSaleId(UUID bulkSaleId);

    List<SaleOffer> findByEnterpriseIdAndStatus(UUID enterpriseId, OfferStatus status, Pageable pageable);

    List<SaleOffer> findByBulkSaleIdAndStatus(UUID bulkSaleId, OfferStatus status);
}
