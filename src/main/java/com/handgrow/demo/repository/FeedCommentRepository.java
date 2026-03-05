package com.handgrow.demo.repository;

import com.handgrow.demo.entity.FeedComment;
import com.handgrow.demo.entity.enums.FeedTargetType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedComment, UUID> {
    List<FeedComment> findByTargetIdAndTargetTypeOrderByCreatedAtDesc(
            UUID targetId, FeedTargetType type, Pageable pageable);

    long countByTargetIdAndTargetType(UUID targetId, FeedTargetType type);
}
