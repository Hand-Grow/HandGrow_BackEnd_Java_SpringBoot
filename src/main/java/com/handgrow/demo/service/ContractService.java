package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateContractRequest;
import com.handgrow.demo.dto.response.DraftContractResponse;
import com.handgrow.demo.dto.response.ElectronicContractResponse;
import java.util.UUID;

public interface ContractService {

    /**
     * Bước 1-3 của Magic Flow: đọc MongoDB chat → gọi AI → trả về form pre-filled.
     *
     * @param roomId MongoDB chat room id
     * @param accountId account đang đăng nhập (để xác minh quyền truy cập phòng)
     * @return DraftContractResponse chứa thông tin hợp đồng được AI trích xuất
     */
    DraftContractResponse aiDraft(String roomId, UUID accountId);

    /**
     * Bước 5: Lưu hợp đồng chính thức vào PostgreSQL sau khi HTX xác nhận.
     *
     * @param accountId account đang đăng nhập
     * @param request body chứa thông tin hợp đồng đã review
     * @return ElectronicContractResponse
     */
    ElectronicContractResponse saveContract(UUID accountId, CreateContractRequest request);

    /**
     * Lấy hợp đồng đã tồn tại của một phòng chat.
     *
     * @param roomId MongoDB chat room id
     * @return ElectronicContractResponse hoặc null nếu chưa tạo
     */
    ElectronicContractResponse getContractByRoom(String roomId);

    /**
     * Lấy danh sách hợp đồng của người dùng đang đăng nhập (HTX hoặc Doanh nghiệp).
     *
     * @param accountId account đang đăng nhập
     * @return Danh sách hợp đồng
     */
    java.util.List<ElectronicContractResponse> getMyContracts(UUID accountId);

    /**
     * Mark enterprise as having signed the contract.
     *
     * @param accountId account performing the signature (enterprise account)
     * @param roomId chat room id identifying the contract
     * @param signatoryName human readable name of the person signing for enterprise
     * @return updated ElectronicContractResponse
     */
    ElectronicContractResponse enterpriseSignContract(UUID accountId, String roomId, String signatoryName);
}
