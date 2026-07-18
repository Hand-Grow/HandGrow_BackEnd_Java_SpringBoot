package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateChatRoomRequest;
import com.handgrow.demo.dto.request.SendChatMessageRequest;
import com.handgrow.demo.dto.response.ChatMessageResponse;
import com.handgrow.demo.dto.response.ChatRoomResponse;
import com.handgrow.demo.service.ChatService;
import com.handgrow.demo.util.SecurityUtils;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    private UUID getAccountId(Principal principal) {
        return SecurityUtils.extractAccountId((org.springframework.security.core.Authentication) principal);
    }

    /**
     * WebSocket Endpoint to send a message to a specific room.
     * The client sends messages to /app/chat.sendMessage/{roomId}
     */
    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(
            @DestinationVariable String roomId, @Payload SendChatMessageRequest request, Principal principal) {
        UUID accountId = getAccountId(principal);
        // The service logic will directly save to Mongo and broadcast it to /topic/room.{roomId} via
        // SimpMessagingTemplate
        chatService.sendMessage(roomId, accountId, request);
    }

    // --- REST Endpoints for Chat History & Contract Management ---

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> getOrCreateRoom(
            @RequestBody CreateChatRoomRequest request, Principal principal) {
        UUID accountId = getAccountId(principal);
        return ResponseEntity.ok(chatService.getOrCreateRoom(accountId, request));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(Principal principal) {
        UUID accountId = getAccountId(principal);
        return ResponseEntity.ok(chatService.getMyRooms(accountId));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable String roomId, Pageable pageable, Principal principal) { // THÊM PRINCIPAL VÀO ĐÂY

        UUID accountId = getAccountId(principal);

        // Xuống Service, bắt buộc phải check: accountId này CÓ QUYỀN vào roomId này không!
        return ResponseEntity.ok(chatService.getMessages(roomId, accountId, pageable));
    }

    // A REST fallback exactly like how it used to be
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessageRest(
            @PathVariable String roomId, @RequestBody SendChatMessageRequest request, Principal principal) {
        UUID accountId = getAccountId(principal);
        return ResponseEntity.ok(chatService.sendMessage(roomId, accountId, request));
    }
}
