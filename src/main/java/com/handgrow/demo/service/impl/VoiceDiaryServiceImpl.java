package com.handgrow.demo.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handgrow.demo.dto.request.CreateDiaryRequest;
import com.handgrow.demo.dto.response.DiaryResponse;
import com.handgrow.demo.dto.response.ProfitResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.dto.response.VoiceDiaryResponse;
import com.handgrow.demo.entity.Farmer;
import com.handgrow.demo.entity.FarmingDiary;
import com.handgrow.demo.entity.Plot;
import com.handgrow.demo.entity.enums.ActivityType;
import com.handgrow.demo.exception.AppException;
import com.handgrow.demo.exception.ErrorCode;
import com.handgrow.demo.repository.FarmerRepository;
import com.handgrow.demo.repository.FarmingDiaryRepository;
import com.handgrow.demo.repository.PlotRepository;
import com.handgrow.demo.service.VoiceDiaryService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VoiceDiaryServiceImpl implements VoiceDiaryService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FarmingDiaryRepository diaryRepository;
    private final PlotRepository plotRepository;
    private final FarmerRepository farmerRepository;

    public VoiceDiaryResponse processVoiceDiary(MultipartFile audioFile) {
        try {
            // 1. Chuẩn bị dữ liệu file
            String base64Audio = Base64.getEncoder().encodeToString(audioFile.getBytes());
            String mimeType = audioFile.getContentType() != null ? audioFile.getContentType() : "audio/mp3";

            // 2. Chuẩn bị Prompt
            String prompt =
                    """
                Bạn là hệ thống AI chuyên gia phân tích dữ liệu nông nghiệp. Nhiệm vụ của bạn là nghe đoạn audio (giọng nông dân Việt Nam, có thể chứa từ lóng, phương ngữ) và trích xuất thông tin thành 1 object JSON duy nhất.

                QUY TẮC BẮT BUỘC (TUYỆT ĐỐI TUÂN THỦ):
                1. KHÔNG TỰ BỊA DỮ LIỆU: Nếu nông dân KHÔNG nhắc đến một thông tin nào đó (ví dụ: giá tiền, số lượng), bắt buộc gán giá trị của field đó là null.
                2. CHUẨN HÓA ĐƠN VỊ (unit): Tự động chuyển đổi lời nói sang đơn vị chuẩn. Ví dụ: "ký/cân" -> "KG", "lít/xị/chai/bình" -> "LITER", "bao/gói" -> "BAG", "tiếng/giờ" -> "HOUR", "công/sào/mẫu" -> "M2" (nếu không biết quy đổi M2 thì giữ nguyên "CONG", "SAO").
                3. TỰ ĐỘNG TÍNH TOÁN: Nếu nông dân đọc số lượng và đơn giá, hãy tự nhân lên để điền vào "expense" (chi phí). Nếu họ đọc tổng tiền, điền thẳng vào "expense".
                4. PHÂN LOẠI CHÍNH XÁC: Áp dụng ĐÚNG 1 trong các activity_type sau và map chi tiết vào object "data":

                - FERTILIZING (Bón phân): {"product_name": string, "quantity": number, "unit": string, "unit_price": number}
                - PESTICIDE (Phun thuốc): {"product_name": string, "quantity": number, "unit": string, "target_pest": string, "unit_price": number}
                - PLANTING (Gieo/Trồng): {"seed_type": string, "quantity": number, "unit": string, "area": number, "area_unit": string}
                - HARVESTING (Thu hoạch): {"product": string, "quantity": number, "unit": string, "unit_price": number, "revenue": number, "buyer": string}
                - WATERING (Tưới nước): {"duration": number, "duration_unit": string, "water_source": string, "electricity_cost": number}
                - WEEDING (Làm cỏ): {"method": "THU_CONG" hoặc "MAY_MOC", "labor_count": number, "wage_per_person": number}

                ĐỊNH DẠNG ĐẦU RA YÊU CẦU:
                Trả về CHỈ MỘT chuỗi JSON thuần túy, bắt đầu bằng '{' và kết thúc bằng '}', KHÔNG SỬ DỤNG markdown (```json). Cấu trúc:
                {
                  "activity_type": "FERTILIZING | PESTICIDE | PLANTING | HARVESTING | WATERING | WEEDING",
                  "expense": Tổng_chi_phí_bằng_số (để 0 nếu là thu hoạch hoặc không tốn tiền, null nếu không rõ),
                  "data": { <object tương ứng với phân loại ở trên> }
                }

                XỬ LÝ LỖI:
                Nếu đoạn audio chỉ có tiếng ồn, không rõ chữ, hoặc nội dung KHÔNG liên quan đến làm nông nghiệp, hãy trả về chính xác chuỗi sau:
                {"error": "invalid content"}
                """;

            // 3. Tạo Object Request
            GeminiRequest requestPayload = GeminiRequest.builder()
                    .contents(Collections.singletonList(Content.builder()
                            .parts(List.of(
                                    Part.builder().text(prompt).build(),
                                    Part.builder()
                                            .inlineData(new InlineData(mimeType, base64Audio))
                                            .build()))
                            .build()))
                    .build();

            // 4. Convert Object -> JSON String
            String jsonBody = objectMapper.writeValueAsString(requestPayload);

            // 5. Gửi Request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // POST lên Google
            String response = restTemplate.postForObject(GEMINI_URL + geminiApiKey, entity, String.class);

            // 6. Xử lý kết quả
            String extractedJson = extractJsonFromGeminiResponse(response);

            return VoiceDiaryResponse.builder()
                    .status("ok")
                    .message("Đã xử lý file ghi âm thành công")
                    .transcription(extractedJson)
                    .build();

        } catch (Exception e) {
            log.error("Error processing voice diary: ", e);
            return VoiceDiaryResponse.builder()
                    .status("error")
                    .message("Lỗi xử lý file: " + e.getMessage())
                    .build();
        }
    }

    private String extractJsonFromGeminiResponse(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        String text = root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        if (text.startsWith("```json")) {
            text = text.replace("```json", "").replace("```", "");
        } else if (text.startsWith("```")) {
            text = text.replace("```", "");
        }
        return text.trim();
    }

    @Transactional
    public DiaryResponse createDiary(String username, CreateDiaryRequest request) {
        Farmer farmer =
                farmerRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Plot plot = plotRepository
                .findById(request.getPlotId())
                .orElseThrow(() -> new AppException(ErrorCode.PLOT_NOT_FOUND));

        if (!plot.getFarmer().getId().equals(farmer.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        FarmingDiary diary = FarmingDiary.builder()
                .plot(plot)
                .farmer(farmer)
                .activityDate(request.getActivityDate())
                .activityType(request.getActivityType())
                .expense(request.getExpense())
                .aiExtractedData(request.getAiExtractedData())
                .originalTranscript(request.getOriginalTranscript())
                .build();

        diary = diaryRepository.save(diary);

        return DiaryResponse.builder()
                .id(diary.getId())
                .plotName(plot.getName())
                .activityDate(diary.getActivityDate())
                .activityType(diary.getActivityType())
                .expense(diary.getExpense())
                .aiExtractedData(diary.getAiExtractedData())
                .originalTranscript(diary.getOriginalTranscript())
                .build();
    }

    @Transactional
    public List<DiaryResponse> getDiariesByPlot(UUID plotId, LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findByPlotIdAndActivityDateBetween(plotId, startDate, endDate).stream()
                .map(d -> DiaryResponse.builder()
                        .id(d.getId())
                        .plotName(d.getPlot().getName())
                        .activityDate(d.getActivityDate())
                        .activityType(d.getActivityType())
                        .expense(d.getExpense())
                        .aiExtractedData(d.getAiExtractedData())
                        .originalTranscript(d.getOriginalTranscript())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public DiaryResponse getDiaryById(UUID diaryId) {
        FarmingDiary diary =
                diaryRepository.findById(diaryId).orElseThrow(() -> new AppException(ErrorCode.DIARY_NOT_FOUND));

        return DiaryResponse.builder()
                .id(diary.getId())
                .plotName(diary.getPlot().getName())
                .activityDate(diary.getActivityDate())
                .activityType(diary.getActivityType())
                .expense(diary.getExpense())
                .aiExtractedData(diary.getAiExtractedData())
                .originalTranscript(diary.getOriginalTranscript())
                .build();
    }

    @Transactional
    public DiaryResponse updateDiary(UUID diaryId, CreateDiaryRequest request) {
        FarmingDiary diary =
                diaryRepository.findById(diaryId).orElseThrow(() -> new AppException(ErrorCode.DIARY_NOT_FOUND));

        diary.setActivityDate(request.getActivityDate());
        diary.setActivityType(request.getActivityType());
        diary.setExpense(request.getExpense());
        diary.setAiExtractedData(request.getAiExtractedData());
        diary.setOriginalTranscript(request.getOriginalTranscript());

        diary = diaryRepository.save(diary);

        return DiaryResponse.builder()
                .id(diary.getId())
                .plotName(diary.getPlot().getName())
                .activityDate(diary.getActivityDate())
                .activityType(diary.getActivityType())
                .expense(diary.getExpense())
                .aiExtractedData(diary.getAiExtractedData())
                .originalTranscript(diary.getOriginalTranscript())
                .build();
    }

    public SimpleResponse deleteDiary(UUID diaryId) {
        diaryRepository.deleteById(diaryId);
        return new SimpleResponse("Đã xóa nhật ký", true);
    }

    @Transactional
    public ProfitResponse calculateProfit(UUID plotId, LocalDate startDate, LocalDate endDate) {
        List<FarmingDiary> diaries = diaryRepository.findByPlotIdAndActivityDateBetween(plotId, startDate, endDate);

        BigDecimal totalRevenue = diaries.stream()
                .filter(d -> d.getActivityType() == ActivityType.HARVESTING)
                .map(d -> extractRevenue(d.getAiExtractedData()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense =
                diaries.stream().map(FarmingDiary::getExpense).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> breakdown = new HashMap<>();
        for (ActivityType type : ActivityType.values()) {
            BigDecimal sum = diaries.stream()
                    .filter(d -> d.getActivityType() == type)
                    .map(FarmingDiary::getExpense)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            breakdown.put(type.name(), sum);
        }

        return ProfitResponse.builder()
                .totalRevenue(totalRevenue)
                .totalExpense(totalExpense)
                .profit(totalRevenue.subtract(totalExpense))
                .expenseBreakdown(breakdown)
                .build();
    }

    private BigDecimal extractRevenue(String jsonData) {
        try {
            JsonNode node = objectMapper.readTree(jsonData);
            return new BigDecimal(node.path("revenue").asText("0"));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    // --- CÁC CLASS DTO ĐỂ TẠO JSON REQUEST ---

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GeminiRequest {
        private List<Content> contents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Content {
        private List<Part> parts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Part {
        private String text;

        @JsonProperty("inline_data")
        private InlineData inlineData;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class InlineData {
        @JsonProperty("mime_type")
        private String mimeType;

        private String data;
    }
}
