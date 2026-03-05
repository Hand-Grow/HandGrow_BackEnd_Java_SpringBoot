package com.handgrow.demo.repository;

import com.handgrow.demo.entity.CoopAnnouncement;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoopAnnouncementRepository extends JpaRepository<CoopAnnouncement, UUID> {
    List<CoopAnnouncement> findByCooperativeIdOrderByCreatedAtDesc(UUID coopId, Pageable pageable);
}
