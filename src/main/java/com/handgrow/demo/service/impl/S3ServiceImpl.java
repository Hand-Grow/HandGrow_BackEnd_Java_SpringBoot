package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.response.PresignedUrlResponse;
import com.handgrow.demo.service.S3Service;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Override
    public PresignedUrlResponse generatePresignedUrl(String originalFilename, String contentType) {
        // 1. Tạo tên file độc nhất để không bị đè file cũ
        String extension =
                originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID().toString() + extension;

        // 2. Yêu cầu tạo URL cho hành động PUT (Upload)
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // URL chỉ có tác dụng trong 15 phút
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        // 3. Đóng gói kết quả trả về
        return PresignedUrlResponse.builder()
                .presignedUrl(presignedRequest.url().toString())
                .publicUrl("https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName)
                .build();
    }
}
