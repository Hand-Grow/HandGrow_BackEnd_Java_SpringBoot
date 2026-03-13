package com.handgrow.demo.service.impl;

import com.handgrow.demo.document.MongoChatMessage;
import com.handgrow.demo.document.MongoChatRoom;
import com.handgrow.demo.dto.request.CreateContractRequest;
import com.handgrow.demo.dto.response.DraftContractResponse;
import com.handgrow.demo.dto.response.ElectronicContractResponse;
import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.BulkSale;
import com.handgrow.demo.entity.Cooperative;
import com.handgrow.demo.entity.ElectronicContract;
import com.handgrow.demo.entity.Enterprise;
import com.handgrow.demo.entity.Farmer;
import com.handgrow.demo.entity.enums.ContractStatus;
import com.handgrow.demo.repository.AccountRepository;
import com.handgrow.demo.repository.BulkSaleRepository;
import com.handgrow.demo.repository.CooperativeRepository;
import com.handgrow.demo.repository.ElectronicContractRepository;
import com.handgrow.demo.repository.EnterpriseRepository;
import com.handgrow.demo.repository.FarmerRepository;
import com.handgrow.demo.repository.MongoChatMessageRepository;
import com.handgrow.demo.repository.MongoChatRoomRepository;
import com.handgrow.demo.service.ContractService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractServiceImpl implements ContractService {

    private final MongoChatRoomRepository chatRoomRepository;
    private final MongoChatMessageRepository chatMessageRepository;
    private final BulkSaleRepository bulkSaleRepository;
    private final CooperativeRepository cooperativeRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final ElectronicContractRepository contractRepository;
    private final AccountRepository accountRepository;
    private final FarmerRepository farmerRepository;
    private final AiContractServiceImpl aiContractService;

    // ─── 1. AI Draft ────────────────────────────────────────────────────────

    @Override
    public DraftContractResponse aiDraft(String roomId, UUID accountId) {
        // Bước 2a: Lấy thông tin phòng chat từ MongoDB
        MongoChatRoom room = chatRoomRepository
                .findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found: " + roomId));

        // Bước 2b: Lấy BulkSale từ PostgreSQL (giữ ngữ cảnh sản phẩm)
        UUID bulkSaleId = UUID.fromString(room.getBulkSaleId());
        BulkSale bulkSale = bulkSaleRepository
                .findById(bulkSaleId)
                .orElseThrow(() -> new RuntimeException("BulkSale not found: " + bulkSaleId));

        // Bước 2c: Lấy 50 tin nhắn gần nhất và ghép thành đoạn text
        List<MongoChatMessage> messages = chatMessageRepository.findTop50ByRoomIdOrderByCreatedAtDesc(roomId);
        String chatHistory = buildChatHistory(messages, room);

        log.info("Sending {} messages to AI for room {}", messages.size(), roomId);

        // Bước 3: Nhờ AI đọc hiểu
        DraftContractResponse draft = aiContractService.extractContractInfo(chatHistory, bulkSale);

        // Gắn thêm metadata để Frontend dùng khi lưu hợp đồng
        draft.setRoomId(roomId);
        draft.setBulkSaleId(bulkSaleId);
        if (room.getCooperativeId() != null) {
            draft.setCooperativeId(UUID.fromString(room.getCooperativeId()));
        }
        if (room.getEnterpriseId() != null) {
            draft.setEnterpriseId(UUID.fromString(room.getEnterpriseId()));
        }

        return draft;
    }

    // ─── 2. Save Contract ────────────────────────────────────────────────────

    @Transactional
    @Override
    public ElectronicContractResponse saveContract(UUID accountId, CreateContractRequest request) {
        String roomId = request.getRoomId();

        // Kiểm tra xem đã có hợp đồng cho phòng này chưa
        if (contractRepository.findByRoomId(roomId).isPresent()) {
            throw new RuntimeException("Hợp đồng cho phòng chat này đã tồn tại: " + roomId);
        }

        // Lấy thông tin phòng chat
        MongoChatRoom room = chatRoomRepository
                .findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found: " + roomId));

        // Lấy các entity liên quan từ PostgreSQL
        BulkSale bulkSale = bulkSaleRepository
                .findById(UUID.fromString(room.getBulkSaleId()))
                .orElseThrow(() -> new RuntimeException("BulkSale not found"));

        Cooperative cooperative = cooperativeRepository
                .findById(UUID.fromString(room.getCooperativeId()))
                .orElseThrow(() -> new RuntimeException("Cooperative not found"));

        Enterprise enterprise = enterpriseRepository
                .findById(UUID.fromString(room.getEnterpriseId()))
                .orElseThrow(() -> new RuntimeException("Enterprise not found"));

        // Xây dựng và lưu hợp đồng
        String terms = buildTerms(request);

        ElectronicContract contract = ElectronicContract.builder()
                .roomId(roomId)
                .bulkSale(bulkSale)
                .cooperative(cooperative)
                .enterprise(enterprise)
                .agreedPrice(request.getAgreedPrice())
                .agreedQuantity(request.getAgreedQuantity())
                .deliveryDate(request.getDeliveryDate())
                .terms(terms)
                .status(ContractStatus.DRAFT)
                .build();

        contract = contractRepository.save(contract);
        log.info("Contract saved with id={} for room={}", contract.getId(), roomId);

        return toContractResponse(contract, bulkSale, cooperative, enterprise);
    }

    // ─── 3. Get Contract By Room ─────────────────────────────────────────────

    @Override
    public ElectronicContractResponse getContractByRoom(String roomId) {
        ElectronicContract contract = contractRepository
                .findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Chưa có hợp đồng nào cho phòng chat: " + roomId));

        BulkSale bulkSale = contract.getBulkSale();
        Cooperative cooperative = contract.getCooperative();
        Enterprise enterprise = contract.getEnterprise();

        return toContractResponse(contract, bulkSale, cooperative, enterprise);
    }

    @Override
    public List<ElectronicContractResponse> getMyContracts(UUID accountId) {
        Account account =
                accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        String role = account.getRole().getName();

        List<ElectronicContract> contracts;
        if (role.equals("ENTERPRISE")) {
            Enterprise enterprise = enterpriseRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Enterprise not found"));
            contracts = contractRepository.findByEnterpriseIdOrderByCreatedAtDesc(enterprise.getId());
        } else if (role.equals("COOP")
                || role.equals("COOPERATIVE")
                || role.equals("ROLE_COOP")
                || role.equals("ROLE_COOPERATIVE")) {
            Cooperative cooperative = cooperativeRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Cooperative config not found"));
            contracts = contractRepository.findByCooperativeIdOrderByCreatedAtDesc(cooperative.getId());
        } else if (role.equals("FARMER")) {
            Farmer farmer = farmerRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Farmer config not found"));
            contracts = contractRepository.findByCooperativeIdOrderByCreatedAtDesc(
                    farmer.getCooperative().getId());
        } else {
            return List.of();
        }

        return contracts.stream()
                .map(c -> toContractResponse(c, c.getBulkSale(), c.getCooperative(), c.getEnterprise()))
                .collect(Collectors.toList());
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────

    /**
     * Biến danh sách tin nhắn thành đoạn hội thoại text theo format:
     * "Bên Bán (HTX ABC): Lô ST25 này..."
     * "Bên Mua (Công ty XYZ): Chốt 15 tấn..."
     */
    private String buildChatHistory(List<MongoChatMessage> messages, MongoChatRoom room) {
        // messages được lấy theo DESC → cần đảo lại thành ASC để AI đọc đúng thứ tự
        List<MongoChatMessage> ordered = messages.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null) return -1;
                    if (b.getCreatedAt() == null) return 1;
                    return a.getCreatedAt().compareTo(b.getCreatedAt());
                })
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (MongoChatMessage msg : ordered) {
            String role = "COOPERATIVE".equalsIgnoreCase(msg.getSenderType()) ? "Bên Bán (HTX)" : "Bên Mua (DN)";
            String name = msg.getSenderName() != null ? msg.getSenderName() : msg.getSenderType();
            sb.append(role)
                    .append(" - ")
                    .append(name)
                    .append(": ")
                    .append(msg.getContent())
                    .append("\n");
        }

        // Thêm context BulkSale để AI có thêm ngữ cảnh về sản phẩm
        sb.append("\n[Thông tin bài đăng bán sỉ gốc - Sản phẩm: ")
                .append(room.getProductName())
                .append(", HTX: ")
                .append(room.getCooperativeName())
                .append(", Doanh nghiệp: ")
                .append(room.getEnterpriseName())
                .append("]");

        return sb.toString();
    }

    /** Gộp terms mặc định + deliveryLocation vào một chuỗi */
    private String buildTerms(CreateContractRequest request) {
        StringBuilder terms = new StringBuilder();
        if (request.getDeliveryLocation() != null
                && !request.getDeliveryLocation().isBlank()) {
            terms.append("Địa điểm giao hàng: ")
                    .append(request.getDeliveryLocation())
                    .append("\n");
        }
        if (request.getTerms() != null && !request.getTerms().isBlank()) {
            terms.append(request.getTerms());
        }
        return terms.toString().strip();
    }

    private ElectronicContractResponse toContractResponse(
            ElectronicContract c, BulkSale bulkSale, Cooperative cooperative, Enterprise enterprise) {

        String enterpriseName = enterprise.getName() != null ? enterprise.getName() : enterprise.getCompanyName();

        return ElectronicContractResponse.builder()
                .id(c.getId())
                .roomId(c.getRoomId())
                .bulkSaleId(bulkSale.getId())
                .productName(bulkSale.getProductName())
                .cooperativeId(cooperative.getId())
                .cooperativeName(cooperative.getName())
                .enterpriseId(enterprise.getId())
                .enterpriseName(enterpriseName)
                .agreedPrice(c.getAgreedPrice())
                .agreedQuantity(c.getAgreedQuantity())
                .deliveryDate(c.getDeliveryDate())
                .terms(c.getTerms())
                .documentUrl(c.getDocumentUrl())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
