package com.handgrow.demo.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handgrow.demo.dto.response.VoiceDiaryResponse;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceDiaryService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VoiceDiaryResponse processVoiceDiary(MultipartFile audioFile) {
        try {
            // 1. Chuẩn bị dữ liệu file
            String base64Audio = Base64.getEncoder().encodeToString(audioFile.getBytes());
            String mimeType = audioFile.getContentType() != null ? audioFile.getContentType() : "audio/mp3";

            // 2. Chuẩn bị Prompt
            String prompt = "Bạn là trợ lý nông nghiệp. Hãy nghe đoạn âm thanh này (giọng nông dân Việt Nam) và trích xuất thông tin nhật ký canh tác thành JSON. " +
                    "Format mẫu: {\"activity_type\": \"FERTILIZE\", \"plot_name\": \"Ruộng A\", \"product\": \"Đạm\", \"quantity\": 50, \"unit\": \"KG\"}. " +
                    "Nếu không nghe rõ, trả về JSON lỗi: {\"error\": \"invalid content\"}. Chỉ trả về JSON thuần, không markdown.";

            // 3. Tạo Object Request
            GeminiRequest requestPayload = GeminiRequest.builder()
                    .contents(Collections.singletonList(
                            Content.builder()
                                    .parts(List.of(
                                            Part.builder().text(prompt).build(),
                                            Part.builder().inlineData(new InlineData(mimeType, base64Audio)).build()
                                    ))
                                    .build()
                    ))
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
        String text = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        if (text.startsWith("```json")) {
            text = text.replace("```json", "").replace("```", "");
        } else if (text.startsWith("```")) {
            text = text.replace("```", "");
        }
        return text.trim();
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