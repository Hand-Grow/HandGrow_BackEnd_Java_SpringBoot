package com.handgrow.demo.repository;

import com.handgrow.demo.entity.BulkSale;
import com.handgrow.demo.entity.enums.BulkSaleStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkSaleRepository extends JpaRepository<BulkSale, UUID> {
    List<BulkSale> findByStatus(BulkSaleStatus status, Pageable pageable);

    List<BulkSale> findByCooperativeId(UUID coopId);

    boolean existsByCampaignId(UUID campaignId);
}
