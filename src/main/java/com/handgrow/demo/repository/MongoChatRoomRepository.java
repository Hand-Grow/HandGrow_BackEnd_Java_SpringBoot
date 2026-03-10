package com.handgrow.demo.repository;

import com.handgrow.demo.document.MongoChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoChatRoomRepository extends MongoRepository<MongoChatRoom, String> {
    Optional<MongoChatRoom> findByBulkSaleIdAndEnterpriseId(String bulkSaleId, String enterpriseId);

    List<MongoChatRoom> findByEnterpriseIdOrderByUpdatedAtDesc(String enterpriseId);

    List<MongoChatRoom> findByCooperativeIdOrderByUpdatedAtDesc(String cooperativeId);
}
