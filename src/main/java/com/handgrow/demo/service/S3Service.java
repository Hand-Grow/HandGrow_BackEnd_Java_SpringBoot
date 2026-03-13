package com.handgrow.demo.service;

import com.handgrow.demo.dto.response.PresignedUrlResponse;

public interface S3Service {
    PresignedUrlResponse generatePresignedUrl(String originalFilename, String contentType);
}
