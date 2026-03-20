package com.handgrow.demo.repository;

import com.handgrow.demo.entity.GroupBuyCampaign;
import com.handgrow.demo.entity.enums.GroupBuyCampaignStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyCampaignRepository extends JpaRepository<GroupBuyCampaign, UUID> {
    Page<GroupBuyCampaign> findByCooperativeId(UUID coopId, Pageable pageable);

    Page<GroupBuyCampaign> findByProductEnterpriseId(UUID enterpriseId, Pageable pageable);

    Page<GroupBuyCampaign> findByStatus(GroupBuyCampaignStatus status, Pageable pageable);
}
