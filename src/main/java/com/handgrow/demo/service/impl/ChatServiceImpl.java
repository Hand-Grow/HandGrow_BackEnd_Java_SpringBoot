package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CreateChatRoomRequest;
import com.handgrow.demo.dto.request.DraftContractRequest;
import com.handgrow.demo.dto.request.SendChatMessageRequest;
import com.handgrow.demo.dto.response.ChatMessageResponse;
import com.handgrow.demo.dto.response.ChatRoomResponse;
import com.handgrow.demo.dto.response.ElectronicContractResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.entity.enums.BulkSaleStatus;
import com.handgrow.demo.entity.enums.ContractStatus;
import com.handgrow.demo.repository.*;
import com.handgrow.demo.service.ChatService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ElectronicContractRepository contractRepository;
    private final BulkSaleRepository bulkSaleRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final AccountRepository accountRepository;
    private final FarmerRepository farmerRepository; // Used to find Coop if account is a Farmer

    @Transactional
    public ChatRoomResponse getOrCreateRoom(UUID accountId, CreateChatRoomRequest request) {
        Account account =
                accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));

        // This endpoint is primarily for Enterprises initiating chat on a BulkSale
        Enterprise enterprise = enterpriseRepository
                .findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Enterprise not found"));

        BulkSale bulkSale = bulkSaleRepository
                .findById(request.getBulkSaleId())
                .orElseThrow(() -> new RuntimeException("BulkSale not found"));

        ChatRoom room = chatRoomRepository
                .findByBulkSaleIdAndEnterpriseId(bulkSale.getId(), enterprise.getId())
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.builder()
                            .bulkSale(bulkSale)
                            .cooperative(bulkSale.getCooperative())
                            .enterprise(enterprise)
                            .status("ACTIVE")
                            .build();
                    return chatRoomRepository.save(newRoom);
                });

        return toChatRoomResponse(room);
    }

    @Transactional
    public List<ChatRoomResponse> getMyRooms(UUID accountId) {
        Account account =
                accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        String role = account.getRole().getName();

        List<ChatRoom> rooms;
        if (role.equals("ENTERPRISE")) {
            Enterprise enterprise = enterpriseRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Enterprise not found"));
            rooms = chatRoomRepository.findByEnterpriseIdOrderByUpdatedAtDesc(enterprise.getId());
        } else if (role.equals("COOPERATIVE") || role.equals("FARMER")) {
            // Assuming Farmer role can act on behalf of Coop if linked (simplified)
            // Ideally, we'd fetch the Coop ID from the Farmer/Account logic
            Farmer farmer = farmerRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Farmer config not found"));
            rooms = chatRoomRepository.findByCooperativeIdOrderByUpdatedAtDesc(
                    farmer.getCooperative().getId());
        } else {
            return List.of();
        }

        return rooms.stream().map(this::toChatRoomResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<ChatMessageResponse> getMessages(UUID roomId, Pageable pageable) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId, pageable).stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatMessageResponse sendMessage(UUID roomId, UUID accountId, SendChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .senderId(accountId)
                .senderType(request.getSenderType())
                .content(request.getContent())
                .build();
        message = chatMessageRepository.save(message);

        return toMessageResponse(message);
    }

    @Transactional
    public ElectronicContractResponse draftContract(UUID roomId, UUID accountId, DraftContractRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        ElectronicContract contract =
                contractRepository.findByChatRoomId(roomId).orElse(null);
        if (contract != null && contract.getStatus() != ContractStatus.DRAFT) {
            throw new RuntimeException("A formal contract already exists and is not in DRAFT state.");
        }

        if (contract == null) {
            contract = ElectronicContract.builder()
                    .chatRoom(room)
                    .bulkSale(room.getBulkSale())
                    .cooperative(room.getCooperative())
                    .enterprise(room.getEnterprise())
                    .build();
        }

        contract.setAgreedPrice(request.getAgreedPrice());
        contract.setAgreedQuantity(request.getAgreedQuantity());
        contract.setDeliveryDate(request.getDeliveryDate());
        contract.setTerms(request.getTerms());

        // Define flow: If enterprise drafts, it's pending coop. If coop drafts, pending enterprise.
        // Simplified: DRAFT requires both to sign anyway, but let's jump to PENDING for the OTHER party.
        Account account =
                accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getRole().getName().equals("ENTERPRISE")) {
            contract.setStatus(ContractStatus.PENDING_COOP_SIGNATURE);
        } else {
            contract.setStatus(ContractStatus.PENDING_ENTERPRISE_SIGNATURE);
        }

        contract = contractRepository.save(contract);
        return toContractResponse(contract);
    }

    @Transactional
    public ElectronicContractResponse getContract(UUID roomId) {
        ElectronicContract contract = contractRepository
                .findByChatRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        return toContractResponse(contract);
    }

    @Transactional
    public SimpleResponse signContract(UUID contractId, UUID accountId) {
        ElectronicContract contract =
                contractRepository.findById(contractId).orElseThrow(() -> new RuntimeException("Contract not found"));
        Account account =
                accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        String role = account.getRole().getName();

        if (role.equals("ENTERPRISE") && contract.getStatus() == ContractStatus.PENDING_ENTERPRISE_SIGNATURE) {
            markContractCompleted(contract);
            return new SimpleResponse("Đã ký hợp đồng", true);
        } else if ((role.equals("COOPERATIVE") || role.equals("FARMER"))
                && contract.getStatus() == ContractStatus.PENDING_COOP_SIGNATURE) {
            markContractCompleted(contract);
            return new SimpleResponse("Đã ký hợp đồng", true);
        }

        throw new RuntimeException("Cannot sign contract at this stage");
    }

    private void markContractCompleted(ElectronicContract contract) {
        contract.setStatus(ContractStatus.COMPLETED);
        contractRepository.save(contract);

        BulkSale bulkSale = contract.getBulkSale();
        bulkSale.setStatus(BulkSaleStatus.SOLD);
        bulkSale.setExpectedPrice(contract.getAgreedPrice()); // Update final price
        bulkSaleRepository.save(bulkSale);

        ChatRoom room = contract.getChatRoom();
        room.setStatus("CLOSED");
        chatRoomRepository.save(room);
    }

    private ChatRoomResponse toChatRoomResponse(ChatRoom room) {
        return ChatRoomResponse.builder()
                .id(room.getId())
                .bulkSaleId(room.getBulkSale().getId())
                .productName(room.getBulkSale().getProductName())
                .cooperativeId(room.getCooperative().getId())
                .cooperativeName(room.getCooperative().getName())
                .enterpriseId(room.getEnterprise().getId())
                .enterpriseName(room.getEnterprise().getName())
                .status(room.getStatus())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    private ChatMessageResponse toMessageResponse(ChatMessage m) {
        String senderName = "Unknown";
        if ("ENTERPRISE".equals(m.getSenderType())) {
            senderName = m.getChatRoom().getEnterprise().getName();
        } else if ("COOPERATIVE".equals(m.getSenderType())) {
            senderName = m.getChatRoom().getCooperative().getName();
        }
        return ChatMessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .senderType(m.getSenderType())
                .senderName(senderName)
                .content(m.getContent())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private ElectronicContractResponse toContractResponse(ElectronicContract c) {
        return ElectronicContractResponse.builder()
                .id(c.getId())
                .roomId(c.getChatRoom().getId())
                .bulkSaleId(c.getBulkSale().getId())
                .productName(c.getBulkSale().getProductName())
                .cooperativeId(c.getCooperative().getId())
                .cooperativeName(c.getCooperative().getName())
                .enterpriseId(c.getEnterprise().getId())
                .enterpriseName(c.getEnterprise().getName())
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
