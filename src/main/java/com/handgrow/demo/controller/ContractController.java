package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateContractRequest;
import com.handgrow.demo.dto.response.DraftContractResponse;
import com.handgrow.demo.dto.response.ElectronicContractResponse;
import com.handgrow.demo.service.ContractService;
import com.handgrow.demo.service.PdfExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private final PdfExportService pdfExportService;

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

    @GetMapping("/my")
    @Operation(
            summary = "Lấy danh sách hợp đồng của tôi",
            description =
                    "Trả về danh sách hợp đồng của HTX hoặc Doanh nghiệp đang đăng nhập. Yêu cầu JWT Authentication.")
    public ResponseEntity<java.util.List<ElectronicContractResponse>> getMyContracts(Authentication authentication) {
        UUID accountId = extractAccountId(authentication);
        log.info("Get my contracts for accountId={}", accountId);
        return ResponseEntity.ok(contractService.getMyContracts(accountId));
    }

    /**
     * Export contract as PDF for a given chat room.
     * GET /api/v1/contracts/room/{roomId}/pdf
     */
    @GetMapping("/room/{roomId}/pdf")
    @Operation(summary = "Tải xuống hợp đồng PDF cho phòng chat (roomId)")
    public ResponseEntity<byte[]> getContractPdf(@PathVariable String roomId) {
        log.info("Export PDF contract for roomId={}", roomId);
        ElectronicContractResponse response = contractService.getContractByRoom(roomId);

        String cooperativeName = response.getCooperativeName() != null ? response.getCooperativeName() : "";
        String enterpriseName = response.getEnterpriseName() != null ? response.getEnterpriseName() : "";

        // Calculate total value = agreedPrice * agreedQuantity, if available
        String totalValue = "0 VNĐ";
        if (response.getAgreedPrice() != null && response.getAgreedQuantity() != null) {
            try {
                BigDecimal total = response.getAgreedPrice().multiply(response.getAgreedQuantity());
                totalValue = total.toPlainString() + " VNĐ";
            } catch (Exception ex) {
                totalValue = "0 VNĐ";
            }
        }

        String aiTerms = response.getTerms() != null ? response.getTerms() : "";

        String enterpriseSignatoryName =
                response.getEnterpriseSignatoryName() != null ? response.getEnterpriseSignatoryName() : "";
        String enterpriseSignDate = response.getEnterpriseSignedAt() != null
                ? response.getEnterpriseSignedAt().toString()
                : "";

        byte[] pdf = pdfExportService.generateContractPdf(
                cooperativeName, enterpriseName, totalValue, aiTerms, enterpriseSignatoryName, enterpriseSignDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "contract-" + roomId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    /**
     * Enterprise signs the contract for a room.
     * POST /api/v1/contracts/room/{roomId}/enterprise-sign
     */
    @PostMapping("/room/{roomId}/enterprise-sign")
    @Operation(summary = "Doanh nghiệp ký hợp đồng (đánh dấu đã ký)")
    public ResponseEntity<ElectronicContractResponse> enterpriseSign(
            @PathVariable String roomId,
            @RequestBody(required = false) java.util.Map<String, String> body,
            Authentication authentication) {
        UUID accountId = extractAccountId(authentication);
        String signatoryName = null;
        if (body != null) {
            signatoryName = body.getOrDefault("signatoryName", null);
        }
        if (signatoryName == null || signatoryName.isBlank()) {
            // fallback: use enterprise representative name from account mapping
            // load enterprise via service
            ElectronicContractResponse existing = contractService.getContractByRoom(roomId);
            signatoryName = existing.getEnterpriseRepresentative();
        }

        ElectronicContractResponse updated = contractService.enterpriseSignContract(accountId, roomId, signatoryName);
        return ResponseEntity.ok(updated);
    }

    /** Trích xuất AccountId (UUID) từ principal đã được Spring Security decode */
    private UUID extractAccountId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof com.handgrow.demo.entity.Account account) {
            return account.getId();
        }
        throw new RuntimeException("User not authenticated correctly");
    }
}
