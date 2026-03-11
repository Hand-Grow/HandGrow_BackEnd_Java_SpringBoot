package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateCommentRequest;
import com.handgrow.demo.dto.response.CommentResponse;
import com.handgrow.demo.dto.response.FeedItemResponse;
import com.handgrow.demo.entity.enums.FeedTargetType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface FeedService {
    List<FeedItemResponse> getFeed(UUID coopId, FeedTargetType type, String username, Pageable pageable);

    void toggleLike(String username, UUID targetId, FeedTargetType type);

    List<CommentResponse> getComments(UUID targetId, FeedTargetType type, Pageable pageable);

    CommentResponse addComment(String username, UUID targetId, FeedTargetType type, CreateCommentRequest request);
}
