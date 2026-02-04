package com.handgrow.demo.repository;

import com.handgrow.demo.entity.Cooperative;
import com.handgrow.demo.entity.Farmer;
import com.handgrow.demo.entity.JoinRequest;
import com.handgrow.demo.entity.enums.JoinRequestStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, UUID> {

    List<JoinRequest> findByCooperativeAndStatus(Cooperative cooperative, JoinRequestStatus status);

    List<JoinRequest> findByFarmerAndStatus(Farmer farmer, JoinRequestStatus status);

    Optional<JoinRequest> findByFarmerAndCooperativeAndStatus(
            Farmer farmer, Cooperative cooperative, JoinRequestStatus status);

    boolean existsByFarmerAndCooperativeAndStatus(Farmer farmer, Cooperative cooperative, JoinRequestStatus status);
}
