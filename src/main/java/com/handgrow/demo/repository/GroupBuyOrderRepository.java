package com.handgrow.demo.repository;

import com.handgrow.demo.entity.GroupBuyOrder;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyOrderRepository extends JpaRepository<GroupBuyOrder, UUID> {
    Optional<GroupBuyOrder> findByCampaignId(UUID campaignId);
}
