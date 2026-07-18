package com.handgrow.demo.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.handgrow.demo.service.FileUploadService;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String publicId = UUID.randomUUID().toString();
        Map uploadResult = cloudinary
                .uploader()
                .upload(file.getBytes(), ObjectUtils.asMap("public_id", publicId, "resource_type", "auto"));
        return uploadResult.get("secure_url").toString();
    }
}
