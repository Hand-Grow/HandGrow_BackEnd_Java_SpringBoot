package com.handgrow.demo.service.impl;

import com.handgrow.demo.document.MongoChatMessage;
import com.handgrow.demo.document.MongoChatRoom;
import com.handgrow.demo.dto.request.CreateChatRoomRequest;
import com.handgrow.demo.dto.request.SendChatMessageRequest;
import com.handgrow.demo.dto.response.ChatMessageResponse;
import com.handgrow.demo.dto.response.ChatRoomResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.repository.*;
import com.handgrow.demo.service.ChatService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final MongoChatRoomRepository chatRoomRepository;
    private final MongoChatMessageRepository chatMessageRepository;
    private final ElectronicContractRepository contractRepository;
    private final BulkSaleRepository bulkSaleRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final AccountRepository accountRepository;
    private final FarmerRepository farmerRepository; // Used to find Coop if account is a Farmer
    private final CooperativeRepository cooperativeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @Override
    public ChatRoomResponse getOrCreateRoom(UUID accountId, CreateChatRoomRequest request) {
        Account account =
                accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));

        Enterprise enterprise = enterpriseRepository
                .findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Enterprise not found"));

        BulkSale bulkSale = bulkSaleRepository
                .findById(request.getBulkSaleId())
                .orElseThrow(() -> new RuntimeException("BulkSale not found"));

        MongoChatRoom room = chatRoomRepository
                .findByBulkSaleIdAndEnterpriseId(
                        bulkSale.getId().toString(), enterprise.getId().toString())
                .orElseGet(() -> {
                    MongoChatRoom newRoom = MongoChatRoom.builder()
                            .bulkSaleId(bulkSale.getId().toString())
                            .productName(bulkSale.getProductName())
                            .cooperativeId(bulkSale.getCooperative().getId().toString())
                            .cooperativeName(bulkSale.getCooperative().getName())
                            .enterpriseId(enterprise.getId().toString())
                            .enterpriseName(enterprise.getName())
                            .status("ACTIVE")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return chatRoomRepository.save(newRoom);
                });

        return toChatRoomResponse(room);
    }

    @Transactional
    @Override
    public List<ChatRoomResponse> getMyRooms(UUID accountId) {
        Account account =
                accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        String role = account.getRole().getName();

        List<MongoChatRoom> rooms;
        if (role.equals("ENTERPRISE")) {
            Enterprise enterprise = enterpriseRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Enterprise not found"));
            log.info("Fetching rooms for ENTERPRISE ID: {}", enterprise.getId());
            rooms = chatRoomRepository.findByEnterpriseIdOrderByUpdatedAtDesc(
                    enterprise.getId().toString());
        } else if (role.equals("COOP")) {
            Cooperative cooperative = cooperativeRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Cooperative config not found"));
            log.info("Fetching rooms for ROLE: {}, COOP ID: {}", role, cooperative.getId());
            rooms = chatRoomRepository.findByCooperativeIdOrderByUpdatedAtDesc(
                    cooperative.getId().toString());
            log.info("Found {} rooms", rooms != null ? rooms.size() : 0);
        } else if (role.equals("FARMER")) {
            Farmer farmer = farmerRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Farmer config not found"));
            rooms = chatRoomRepository.findByCooperativeIdOrderByUpdatedAtDesc(
                    farmer.getCooperative().getId().toString());
        } else {
            log.warn("Role {} did not match any condition in getMyRooms. Returning empty list.", role);
            return List.of();
        }

        return rooms.stream().map(this::toChatRoomResponse).collect(Collectors.toList());
    }

    private void validateUserAccessToRoom(MongoChatRoom room, UUID accountId) {
        Account account =
                accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        String role = account.getRole().getName();

        if ("ENTERPRISE".equals(role)) {
            Enterprise enterprise = enterpriseRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Enterprise not found"));
            // Nếu Doanh nghiệp đăng nhập không khớp với Doanh nghiệp của phòng chat -> CÚT!
            if (room.getEnterpriseId() == null
                    || !room.getEnterpriseId().equals(enterprise.getId().toString())) {
                log.error(
                        "DEBUG ENTERPRISE: Room Ent ID: {} != User Ent ID: {}",
                        room.getEnterpriseId(),
                        enterprise.getId());
                throw new RuntimeException("DEBUG ENTERPRISE: Room Ent ID: " + room.getEnterpriseId()
                        + " != User Ent ID: " + enterprise.getId());
            }
        } else if ("COOP".equals(role)
                || "COOPERATIVE".equals(role)
                || "ROLE_COOP".equals(role)
                || "ROLE_COOPERATIVE".equals(role)) {
            Cooperative cooperative = cooperativeRepository
                    .findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Cooperative not found"));
            // Nếu HTX đăng nhập không khớp với HTX của phòng chat -> CÚT!
            if (room.getCooperativeId() == null
                    || !room.getCooperativeId().equals(cooperative.getId().toString())) {
                log.error(
                        "DEBUG COOPERATIVE: Room Coop ID: {} != User Coop ID: {}",
                        room.getCooperativeId(),
                        cooperative.getId());
                throw new RuntimeException("DEBUG COOPERATIVE: Room Coop ID: " + room.getCooperativeId()
                        + " != User Coop ID: " + cooperative.getId());
            }
        } else if ("FARMER".equals(role)) {
            Farmer farmer =
                    farmerRepository.findByAccount(account).orElseThrow(() -> new RuntimeException("Farmer not found"));
            // Nông dân được xem phòng chat của HTX mình
            if (room.getCooperativeId() == null
                    || !room.getCooperativeId()
                            .equals(farmer.getCooperative().getId().toString())) {
                log.error(
                        "DEBUG FARMER: Room Coop ID: {} != Farmer Coop ID: {}",
                        room.getCooperativeId(),
                        farmer.getCooperative().getId());
                throw new RuntimeException("DEBUG FARMER: Room Coop ID: " + room.getCooperativeId()
                        + " != Farmer Coop ID: " + farmer.getCooperative().getId());
            }
        } else {
            log.error("Access Denied: Role {} không hợp lệ!", role);
            throw new RuntimeException("Access Denied: Role không hợp lệ!");
        }
    }

    @Override
    public List<ChatMessageResponse> getMessages(String roomId, UUID accountId, Pageable pageable) {
        // 1. Tìm phòng chat trước
        MongoChatRoom room = chatRoomRepository
                .findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        // 2. CHẶN CỬA: Kiểm tra quyền truy cập!
        try {
            validateUserAccessToRoom(room, accountId);
        } catch (RuntimeException ex) {
            return List.of(ChatMessageResponse.builder()
                    .id("debug-error")
                    .senderType("SYSTEM")
                    .senderName("Access Denied Log")
                    .content(ex.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        // 3. Nếu qua ải an toàn, mới lôi data ra trả về
        List<MongoChatMessage> msgs = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId, pageable);
        if (msgs.isEmpty()) {
            return List.of(ChatMessageResponse.builder()
                    .id("debug-empty")
                    .senderType("SYSTEM")
                    .senderName("System")
                    .content("Không có tin nhắn nào trong phòng chat này. Hãy thử nhắn một tin mới!")
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        return msgs.stream().map(this::toMessageResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ChatMessageResponse sendMessage(String roomId, UUID accountId, SendChatMessageRequest request) {
        // 1. Tìm phòng chat
        MongoChatRoom room =
                chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        // 2. CHẶN CỬA: Ngăn chặn gửi tin nhắn mạo danh vào phòng người khác!
        validateUserAccessToRoom(room, accountId);

        String senderName = "Unknown";
        if ("ENTERPRISE".equals(request.getSenderType())) {
            senderName = room.getEnterpriseName();
        } else if ("COOPERATIVE".equals(request.getSenderType())) {
            senderName = room.getCooperativeName();
        }

        MongoChatMessage message = MongoChatMessage.builder()
                .roomId(roomId)
                .senderId(accountId)
                .senderType(request.getSenderType())
                .senderName(senderName)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        message = chatMessageRepository.save(message);

        room.setUpdatedAt(LocalDateTime.now());
        chatRoomRepository.save(room);

        ChatMessageResponse response = toMessageResponse(message);

        try {
            messagingTemplate.convertAndSend("/topic/room." + roomId, response);
        } catch (Exception e) {
            log.error("Failed to broadcast message to room {}", roomId, e);
        }

        return response;
    }

    private ChatRoomResponse toChatRoomResponse(MongoChatRoom room) {
        return ChatRoomResponse.builder()
                .id(room.getId())
                .bulkSaleId(room.getBulkSaleId() != null ? UUID.fromString(room.getBulkSaleId()) : null)
                .productName(room.getProductName())
                .cooperativeId(room.getCooperativeId() != null ? UUID.fromString(room.getCooperativeId()) : null)
                .cooperativeName(room.getCooperativeName())
                .enterpriseId(room.getEnterpriseId() != null ? UUID.fromString(room.getEnterpriseId()) : null)
                .enterpriseName(room.getEnterpriseName())
                .status(room.getStatus())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    private ChatMessageResponse toMessageResponse(MongoChatMessage m) {
        return ChatMessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .senderType(m.getSenderType())
                .senderName(m.getSenderName())
                .content(m.getContent())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
