package com.handgrow.demo.repository;

import com.handgrow.demo.entity.SourcingRequest;
import com.handgrow.demo.entity.SourcingRequest.SourcingRequestStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SourcingRequestRepository extends JpaRepository<SourcingRequest, UUID> {

    Page<SourcingRequest> findByStatus(SourcingRequestStatus status, Pageable pageable);

    Page<SourcingRequest> findByEnterpriseId(UUID enterpriseId, Pageable pageable);

    @Query("SELECT sr FROM SourcingRequest sr WHERE "
            + "(:productName IS NULL OR LOWER(sr.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND "
            + "(:status IS NULL OR sr.status = :status)")
    Page<SourcingRequest> searchSourcingRequests(
            @Param("productName") String productName, @Param("status") SourcingRequestStatus status, Pageable pageable);

    List<SourcingRequest> findByEnterpriseIdAndStatus(UUID enterpriseId, SourcingRequestStatus status);

    @Query(
            "SELECT sr FROM SourcingRequest sr WHERE sr.deadline < CURRENT_DATE AND sr.status IN ('OPEN', 'IN_PROGRESS')")
    List<SourcingRequest> findExpiredRequests();
}
