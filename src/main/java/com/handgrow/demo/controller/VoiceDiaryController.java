package com.handgrow.demo.controller;

import com.handgrow.demo.dto.response.VoiceDiaryResponse;
import com.handgrow.demo.service.impl.VoiceDiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/voice-diary")
@RequiredArgsConstructor
public class VoiceDiaryController {

    private final VoiceDiaryService voiceDiaryService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload voice diary", description = "Upload MP3 audio file to convert to diary entry")
    public ResponseEntity<VoiceDiaryResponse> uploadVoiceDiary(
            @Parameter(description = "Audio file (MP3, WAV, etc.)", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("audio") MultipartFile audioFile) {
        
        if (audioFile.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(VoiceDiaryResponse.builder()
                            .status("error")
                            .message("File must not be empty")
                            .build());
        }

        VoiceDiaryResponse response = voiceDiaryService.processVoiceDiary(audioFile);
        return ResponseEntity.ok(response);
    }
}
