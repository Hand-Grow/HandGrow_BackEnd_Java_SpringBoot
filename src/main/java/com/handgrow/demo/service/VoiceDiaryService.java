package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateDiaryRequest;
import com.handgrow.demo.dto.response.DiaryResponse;
import com.handgrow.demo.dto.response.ProfitResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.dto.response.VoiceDiaryResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface VoiceDiaryService {
    VoiceDiaryResponse processVoiceDiary(MultipartFile audioFile);

    DiaryResponse createDiary(String username, CreateDiaryRequest request);

    List<DiaryResponse> getDiariesByPlot(UUID plotId, LocalDate startDate, LocalDate endDate);

    DiaryResponse getDiaryById(UUID diaryId);

    DiaryResponse updateDiary(UUID diaryId, CreateDiaryRequest request);

    SimpleResponse deleteDiary(UUID diaryId);

    ProfitResponse calculateProfit(UUID plotId, LocalDate startDate, LocalDate endDate);
}
