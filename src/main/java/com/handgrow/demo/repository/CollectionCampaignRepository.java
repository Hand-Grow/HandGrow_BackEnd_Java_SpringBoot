package com.handgrow.demo.repository;

import com.handgrow.demo.entity.CollectionCampaign;
import com.handgrow.demo.entity.enums.CampaignStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionCampaignRepository extends JpaRepository<CollectionCampaign, UUID> {
    List<CollectionCampaign> findByCooperativeIdOrderByCreatedAtDesc(UUID coopId, Pageable pageable);

    List<CollectionCampaign> findByCooperativeIdAndStatus(UUID coopId, CampaignStatus status);
}
