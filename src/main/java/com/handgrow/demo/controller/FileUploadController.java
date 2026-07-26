package com.handgrow.demo.controller;

import com.handgrow.demo.dto.response.ApiResponse;
import com.handgrow.demo.dto.response.PresignedUrlResponse;
import com.handgrow.demo.service.FileUploadService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(
            @RequestParam(required = false) String extension,
            @RequestParam(required = false, defaultValue = "image/jpeg") String contentType) {

        PresignedUrlResponse response = fileUploadService.generatePresignedUrl(extension, contentType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadFile(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
