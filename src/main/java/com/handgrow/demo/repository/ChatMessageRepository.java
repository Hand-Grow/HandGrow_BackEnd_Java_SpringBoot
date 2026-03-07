package com.handgrow.demo.repository;

import com.handgrow.demo.entity.ChatMessage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(UUID roomId, Pageable pageable);
}
