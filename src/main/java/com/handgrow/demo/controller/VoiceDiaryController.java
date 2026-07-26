package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateDiaryRequest;
import com.handgrow.demo.dto.response.ApiResponse;
import com.handgrow.demo.dto.response.DiaryResponse;
import com.handgrow.demo.dto.response.ProfitResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.dto.response.VoiceDiaryResponse;
import com.handgrow.demo.service.VoiceDiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<ApiResponse<VoiceDiaryResponse>> uploadVoiceDiary(
            @Parameter(
                            description = "Audio file (MP3, WAV, etc.)",
                            required = true,
                            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                    @RequestParam("audio")
                    MultipartFile audioFile) {

        if (audioFile.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "File must not be empty"));
        }

        VoiceDiaryResponse response = voiceDiaryService.processVoiceDiary(audioFile);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create diary entry")
    public ResponseEntity<ApiResponse<DiaryResponse>> createDiary(
            Principal principal, @RequestBody CreateDiaryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(voiceDiaryService.createDiary(principal.getName(), request)));
    }

    @GetMapping("/plot/{plotId}")
    @Operation(summary = "Get diaries by plot")
    public ResponseEntity<ApiResponse<List<DiaryResponse>>> getDiariesByPlot(
            @PathVariable UUID plotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(voiceDiaryService.getDiariesByPlot(plotId, startDate, endDate)));
    }

    @GetMapping("/{diaryId}")
    @Operation(summary = "Get diary by ID")
    public ResponseEntity<ApiResponse<DiaryResponse>> getDiaryById(@PathVariable UUID diaryId) {
        return ResponseEntity.ok(ApiResponse.success(voiceDiaryService.getDiaryById(diaryId)));
    }

    @PutMapping("/{diaryId}")
    @Operation(summary = "Update diary")
    public ResponseEntity<ApiResponse<DiaryResponse>> updateDiary(
            @PathVariable UUID diaryId, @RequestBody CreateDiaryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(voiceDiaryService.updateDiary(diaryId, request)));
    }

    @DeleteMapping("/{diaryId}")
    @Operation(summary = "Delete diary")
    public ResponseEntity<ApiResponse<SimpleResponse>> deleteDiary(@PathVariable UUID diaryId) {
        return ResponseEntity.ok(ApiResponse.success(voiceDiaryService.deleteDiary(diaryId)));
    }

    @GetMapping("/plot/{plotId}/profit")
    @Operation(summary = "Calculate profit")
    public ResponseEntity<ApiResponse<ProfitResponse>> calculateProfit(
            @PathVariable UUID plotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(voiceDiaryService.calculateProfit(plotId, startDate, endDate)));
    }
}
