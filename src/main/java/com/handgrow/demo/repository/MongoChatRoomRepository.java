package com.handgrow.demo.repository;

import com.handgrow.demo.document.MongoChatRoom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoChatRoomRepository extends MongoRepository<MongoChatRoom, String> {
    Optional<MongoChatRoom> findByBulkSaleIdAndEnterpriseId(UUID bulkSaleId, UUID enterpriseId);

    List<MongoChatRoom> findByEnterpriseIdOrderByUpdatedAtDesc(UUID enterpriseId);

    List<MongoChatRoom> findByCooperativeIdOrderByUpdatedAtDesc(UUID cooperativeId);
}
