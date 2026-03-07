package com.handgrow.demo.repository;

import com.handgrow.demo.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findByBulkSaleIdAndEnterpriseId(UUID bulkSaleId, UUID enterpriseId);

    List<ChatRoom> findByEnterpriseIdOrderByUpdatedAtDesc(UUID enterpriseId);

    List<ChatRoom> findByCooperativeIdOrderByUpdatedAtDesc(UUID cooperativeId);
}
