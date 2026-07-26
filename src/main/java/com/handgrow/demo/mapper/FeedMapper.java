package com.handgrow.demo.mapper;

import com.handgrow.demo.dto.response.CommentResponse;
import com.handgrow.demo.dto.response.FeedItemResponse;
import com.handgrow.demo.entity.CollectionCampaign;
import com.handgrow.demo.entity.CoopAnnouncement;
import com.handgrow.demo.entity.FeedComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true))
public interface FeedMapper {

    @Mapping(target = "type", constant = "ANNOUNCEMENT")
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "commentCount", target = "commentCount")
    @Mapping(source = "isLiked", target = "liked")
    FeedItemResponse toFeedItemResponse(
            CoopAnnouncement announcement, long likeCount, long commentCount, boolean isLiked);

    @Mapping(target = "type", constant = "CAMPAIGN")
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "commentCount", target = "commentCount")
    @Mapping(source = "isLiked", target = "liked")
    @Mapping(source = "isPublished", target = "published")
    FeedItemResponse toFeedItemResponse(
            CollectionCampaign campaign, long likeCount, long commentCount, boolean isLiked, boolean isPublished);

    @Mapping(source = "account.username", target = "farmerName")
    CommentResponse toCommentResponse(FeedComment comment);
}
