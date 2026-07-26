package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.response.PresignedUrlResponse;
import com.handgrow.demo.service.FileUploadService;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name:handgrow-bucket}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // Optional: Implement direct upload to S3 here if needed
        throw new UnsupportedOperationException("Use presigned URL for direct upload");
    }

    @Override
    public PresignedUrlResponse generatePresignedUrl(String extension, String contentType) {
        String fileKey = UUID.randomUUID().toString() + (extension != null ? "." + extension : "");

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return PresignedUrlResponse.builder()
                .uploadUrl(presignedRequest.url().toString())
                .fileKey(fileKey)
                .build();
    }
}
