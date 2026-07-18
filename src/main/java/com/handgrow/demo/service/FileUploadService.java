package com.handgrow.demo.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadFile(MultipartFile file) throws IOException;
}
