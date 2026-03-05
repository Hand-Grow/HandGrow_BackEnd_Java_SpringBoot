package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateCommentRequest;
import com.handgrow.demo.dto.response.CommentResponse;
import com.handgrow.demo.dto.response.FeedItemResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.entity.enums.FeedTargetType;
import com.handgrow.demo.service.FeedService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/coops/{coopId}/feed")
    public ResponseEntity<List<FeedItemResponse>> getFeed(
            @PathVariable UUID coopId, Principal principal, Pageable pageable) {
        UUID farmerId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(feedService.getFeed(coopId, farmerId, pageable));
    }

    @PostMapping("/feed/{type}/{id}/likes")
    public ResponseEntity<SimpleResponse> toggleLike(
            @PathVariable String type, @PathVariable UUID id, Principal principal) {
        UUID farmerId = UUID.fromString(principal.getName());
        feedService.toggleLike(farmerId, id, FeedTargetType.valueOf(type.toUpperCase()));
        return ResponseEntity.ok(new SimpleResponse("Đã thả tim", true));
    }

    @GetMapping("/feed/{type}/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable String type, @PathVariable UUID id, Pageable pageable) {
        return ResponseEntity.ok(feedService.getComments(id, FeedTargetType.valueOf(type.toUpperCase()), pageable));
    }

    @PostMapping("/feed/{type}/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String type,
            @PathVariable UUID id,
            @RequestBody CreateCommentRequest request,
            Principal principal) {
        UUID farmerId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(
                feedService.addComment(farmerId, id, FeedTargetType.valueOf(type.toUpperCase()), request));
    }
}
