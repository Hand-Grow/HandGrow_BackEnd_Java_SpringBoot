package com.handgrow.demo.repository;

import com.handgrow.demo.entity.GroupBuyParticipation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyParticipationRepository extends JpaRepository<GroupBuyParticipation, UUID> {
    List<GroupBuyParticipation> findByCampaignId(UUID campaignId);
    Optional<GroupBuyParticipation> findByCampaignIdAndFarmerId(UUID campaignId, UUID farmerId);
    
    @Query("SELECT SUM(p.committedQty) FROM GroupBuyParticipation p WHERE p.campaign.id = :campaignId")
    BigDecimal sumCommittedQtyByCampaignId(UUID campaignId);
}
