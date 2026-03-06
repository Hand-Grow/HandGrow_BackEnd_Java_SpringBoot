package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CreateCommentRequest;
import com.handgrow.demo.dto.response.CommentResponse;
import com.handgrow.demo.dto.response.FeedItemResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.entity.enums.FeedTargetType;
import com.handgrow.demo.repository.*;
import com.handgrow.demo.service.FeedService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final CoopAnnouncementRepository announcementRepository;
    private final CollectionCampaignRepository campaignRepository;
    private final FeedLikeRepository likeRepository;
    private final FeedCommentRepository commentRepository;
    private final FarmerRepository farmerRepository;

    @Transactional
    public List<FeedItemResponse> getFeed(UUID coopId, String username, Pageable pageable) {
        Farmer farmer =
                farmerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Farmer not found"));
        UUID farmerId = farmer.getId();

        List<FeedItemResponse> feed = new ArrayList<>();

        List<CoopAnnouncement> announcements =
                announcementRepository.findByCooperativeIdOrderByCreatedAtDesc(coopId, pageable);
        for (CoopAnnouncement a : announcements) {
            feed.add(FeedItemResponse.builder()
                    .id(a.getId())
                    .type(FeedTargetType.ANNOUNCEMENT)
                    .title(a.getTitle())
                    .content(a.getContent())
                    .likeCount(likeRepository.countByTargetIdAndTargetType(a.getId(), FeedTargetType.ANNOUNCEMENT))
                    .commentCount(
                            commentRepository.countByTargetIdAndTargetType(a.getId(), FeedTargetType.ANNOUNCEMENT))
                    .isLiked(likeRepository
                            .findByFarmerIdAndTargetIdAndTargetType(farmerId, a.getId(), FeedTargetType.ANNOUNCEMENT)
                            .isPresent())
                    .createdAt(a.getCreatedAt())
                    .build());
        }

        List<CollectionCampaign> campaigns =
                campaignRepository.findByCooperativeIdOrderByCreatedAtDesc(coopId, pageable);
        for (CollectionCampaign c : campaigns) {
            feed.add(FeedItemResponse.builder()
                    .id(c.getId())
                    .type(FeedTargetType.CAMPAIGN)
                    .title(c.getProductName())
                    .content("Ngày dự kiến: " + c.getExpectedDate())
                    .likeCount(likeRepository.countByTargetIdAndTargetType(c.getId(), FeedTargetType.CAMPAIGN))
                    .commentCount(commentRepository.countByTargetIdAndTargetType(c.getId(), FeedTargetType.CAMPAIGN))
                    .isLiked(likeRepository
                            .findByFarmerIdAndTargetIdAndTargetType(farmerId, c.getId(), FeedTargetType.CAMPAIGN)
                            .isPresent())
                    .createdAt(c.getCreatedAt())
                    .build());
        }

        return feed.stream()
                .sorted(Comparator.comparing(FeedItemResponse::getCreatedAt).reversed())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleLike(String username, UUID targetId, FeedTargetType type) {
        Farmer farmer =
                farmerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Farmer not found"));
        UUID farmerId = farmer.getId();

        var existing = likeRepository.findByFarmerIdAndTargetIdAndTargetType(farmerId, targetId, type);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
        } else {
            likeRepository.save(FeedLike.builder()
                    .farmer(farmer)
                    .targetId(targetId)
                    .targetType(type)
                    .build());
        }
    }

    @Transactional
    public List<CommentResponse> getComments(UUID targetId, FeedTargetType type, Pageable pageable) {
        return commentRepository.findByTargetIdAndTargetTypeOrderByCreatedAtDesc(targetId, type, pageable).stream()
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .farmerName(c.getFarmer().getFullName())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse addComment(
            String username, UUID targetId, FeedTargetType type, CreateCommentRequest request) {
        Farmer farmer =
                farmerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Farmer not found"));

        FeedComment comment = commentRepository.save(FeedComment.builder()
                .farmer(farmer)
                .targetId(targetId)
                .targetType(type)
                .content(request.getContent())
                .build());

        return CommentResponse.builder()
                .id(comment.getId())
                .farmerName(farmer.getFullName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
