package com.handgrow.demo.repository;

import com.handgrow.demo.entity.FeedLike;
import com.handgrow.demo.entity.enums.FeedTargetType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, UUID> {
    Optional<FeedLike> findByAccountIdAndTargetIdAndTargetType(UUID accountId, UUID targetId, FeedTargetType type);

    long countByTargetIdAndTargetType(UUID targetId, FeedTargetType type);

    void deleteByAccountIdAndTargetIdAndTargetType(UUID accountId, UUID targetId, FeedTargetType type);
}
