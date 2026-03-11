package com.handgrow.demo.controller;

import com.handgrow.demo.dto.response.PresignedUrlResponse;
import com.handgrow.demo.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final S3Service s3Service;

    // Ví dụ: GET /api/v1/files/presigned-url?filename=cay_lua.jpg&contentType=image/jpeg
    @GetMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> getPresignedUrl(
            @RequestParam String filename, @RequestParam String contentType) {

        return ResponseEntity.ok(s3Service.generatePresignedUrl(filename, contentType));
    }
}
