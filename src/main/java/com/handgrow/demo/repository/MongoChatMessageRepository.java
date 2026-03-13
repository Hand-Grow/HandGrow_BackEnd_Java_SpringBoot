package com.handgrow.demo.repository;

import com.handgrow.demo.document.MongoChatMessage;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoChatMessageRepository extends MongoRepository<MongoChatMessage, String> {
    List<MongoChatMessage> findByRoomIdOrderByCreatedAtAsc(String roomId, Pageable pageable);

    List<MongoChatMessage> findTop50ByRoomIdOrderByCreatedAtDesc(String roomId);
}
