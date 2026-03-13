package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateContractRequest;
import com.handgrow.demo.dto.response.DraftContractResponse;
import com.handgrow.demo.dto.response.ElectronicContractResponse;
import com.handgrow.demo.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contract", description = "API quản lý hợp đồng điện tử và AI drafting")
public class ContractController {

    private final ContractService contractService;

    @PostMapping("/ai-draft/{roomId}")
    @Operation(
            summary = "AI tự động trích xuất thông tin hợp đồng từ lịch sử chat",
            description =
                    "Đọc 50 tin nhắn gần nhất trong phòng chat, gửi lên Gemini AI để phân tích, trả về JSON pre-filled. "
                            + "Nếu Gemini không sẵn sàng, fallback dữ liệu từ BulkSale. Yêu cầu JWT Authentication.")
    public ResponseEntity<DraftContractResponse> aiDraft(@PathVariable String roomId, Authentication authentication) {
        UUID accountId = extractAccountId(authentication);
        log.info("AI draft request for roomId={} by accountId={}", roomId, accountId);
        DraftContractResponse draft = contractService.aiDraft(roomId, accountId);
        return ResponseEntity.ok(draft);
    }

    /**
     * Lưu hợp đồng chính thức sau khi HTX đã review và xác nhận form.
     *
     * POST /api/v1/contracts
     */
    @PostMapping
    @Operation(
            summary = "Lưu hợp đồng chính thức vào PostgreSQL",
            description =
                    "Sau khi HTX đã kiểm tra và sửa thông tin trên form, gọi endpoint này để lưu hợp đồng điện tử. "
                            + "Yêu cầu JWT Authentication.")
    public ResponseEntity<ElectronicContractResponse> saveContract(
            @Valid @RequestBody CreateContractRequest request, Authentication authentication) {
        UUID accountId = extractAccountId(authentication);
        log.info("Save contract request for roomId={} by accountId={}", request.getRoomId(), accountId);
        ElectronicContractResponse response = contractService.saveContract(accountId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy hợp đồng đã tồn tại của một phòng chat.
     *
     * GET /api/v1/contracts/room/{roomId}
     */
    @GetMapping("/room/{roomId}")
    @Operation(
            summary = "Lấy hợp đồng điện tử theo phòng chat",
            description = "Trả về hợp đồng đã được lưu trước đó cho roomId này. Yêu cầu JWT Authentication.")
    public ResponseEntity<ElectronicContractResponse> getContractByRoom(@PathVariable String roomId) {
        log.info("Get contract for roomId={}", roomId);
        ElectronicContractResponse response = contractService.getContractByRoom(roomId);
        return ResponseEntity.ok(response);
    }

    /** Trích xuất AccountId (UUID) từ JWT token đã được Spring Security decode */
    private UUID extractAccountId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
