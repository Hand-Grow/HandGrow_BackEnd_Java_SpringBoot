package com.handgrow.demo.service;

import com.handgrow.demo.dto.response.PresignedUrlResponse;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadFile(MultipartFile file) throws IOException;

    PresignedUrlResponse generatePresignedUrl(String extension, String contentType);
}
