package com.handgrow.demo.repository;

import com.handgrow.demo.entity.ElectronicContract;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectronicContractRepository extends JpaRepository<ElectronicContract, UUID> {
    Optional<ElectronicContract> findByRoomId(String roomId);
}
