package com.handgrow.demo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handgrow.demo.dto.response.DraftContractResponse;
import com.handgrow.demo.entity.BulkSale;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiContractServiceImpl {

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper;

    /**
     * Trích xuất thông tin hợp đồng từ lịch sử chat.
     *
     * @param chatHistory đoạn text lịch sử chat đã ghép
     * @param bulkSale dùng để fallback nếu Gemini không hoạt động
     * @return DraftContractResponse với các trường đã điền (hoặc fallback)
     */
    public DraftContractResponse extractContractInfo(String chatHistory, BulkSale bulkSale) {
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            try {
                return callGemini(chatHistory, bulkSale);
            } catch (Exception e) {
                log.warn("Gemini API call failed, falling back to BulkSale data. Error: {}", e.getMessage());
            }
        } else {
            log.info("Gemini API key not configured. Using BulkSale fallback.");
        }
        return buildFallback(bulkSale);
    }

    // ─── Private: Gọi Gemini ────────────────────────────────────────────────

    private DraftContractResponse callGemini(String chatHistory, BulkSale bulkSale) throws Exception {
        String prompt = buildPrompt(chatHistory);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        WebClient client = WebClient.builder()
                .baseUrl(GEMINI_API_URL + "?key=" + geminiApiKey)
                .build();

        String rawResponse = client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.debug("Gemini raw response: {}", rawResponse);
        return parseGeminiResponse(rawResponse, bulkSale);
    }

    /** Build prompt yêu cầu Gemini chỉ trả về JSON */
    private String buildPrompt(String chatHistory) {
        return "Bạn là một trợ lý luật sư chuyên về hợp đồng mua bán nông sản. "
                + "Hãy đọc đoạn hội thoại mua bán nông sản sau và trích xuất thông tin hợp đồng. "
                + "CHỈ TRẢ VỀ ĐỊNH DẠNG JSON MÀ KHÔNG CÓ BẤT KỲ VĂN BẢN NÀO KHÁC, "
                + "không có markdown, không có ```json, không có giải thích. "
                + "Cấu trúc JSON bắt buộc (tất cả giá trị đều là string, nếu không tìm thấy thông tin hãy để chuỗi rỗng \"\"):\n"
                + "{\"productName\": \"\", \"quantity\": \"\", \"unitPrice\": \"\", "
                + "\"deliveryDate\": \"\", \"deliveryLocation\": \"\"}\n\n"
                + "Đoạn hội thoại:\n"
                + chatHistory;
    }

    /** Parse JSON từ response của Gemini */
    private DraftContractResponse parseGeminiResponse(String rawResponse, BulkSale bulkSale) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);
        // Lấy text từ candidates[0].content.parts[0].text
        String text = root.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text")
                .asText("");

        // Làm sạch markdown code fence nếu Gemini vẫn wrap ```json ... ```
        text = text.strip();
        if (text.startsWith("```")) {
            text = text.replaceAll("^```[a-zA-Z]*\\n?", "")
                    .replaceAll("```$", "")
                    .strip();
        }

        log.debug("Extracted JSON from Gemini: {}", text);
        JsonNode data = objectMapper.readTree(text);

        return DraftContractResponse.builder()
                .productName(data.path("productName").asText(bulkSale.getProductName()))
                .quantity(data.path("quantity").asText(""))
                .unitPrice(data.path("unitPrice").asText(""))
                .deliveryDate(data.path("deliveryDate").asText(""))
                .deliveryLocation(data.path("deliveryLocation").asText(""))
                .aiGenerated(true)
                .build();
    }

    // ─── Private: Fallback từ BulkSale ──────────────────────────────────────

    private DraftContractResponse buildFallback(BulkSale bulkSale) {
        return DraftContractResponse.builder()
                .productName(bulkSale.getProductName())
                .quantity(
                        bulkSale.getTotalQuantity() != null
                                ? bulkSale.getTotalQuantity().toPlainString()
                                : "")
                .unitPrice(
                        bulkSale.getExpectedPrice() != null
                                ? bulkSale.getExpectedPrice().toPlainString()
                                : "")
                .deliveryDate("")
                .deliveryLocation("")
                .aiGenerated(false)
                .build();
    }
}
