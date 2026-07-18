package com.handgrow.demo.repository;

import com.handgrow.demo.entity.CollectionCommitment;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionCommitmentRepository extends JpaRepository<CollectionCommitment, UUID> {
    List<CollectionCommitment> findByCampaignId(UUID campaignId, Pageable pageable);

    boolean existsByCampaignIdAndFarmerId(UUID campaignId, UUID farmerId);

    List<CollectionCommitment> findByCampaignIdAndFarmerId(UUID campaignId, UUID farmerId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.committedQuantity), 0) FROM CollectionCommitment c WHERE c.campaign.id = :campaignId")
    BigDecimal sumQuantityByCampaign(@Param("campaignId") UUID campaignId);
}
