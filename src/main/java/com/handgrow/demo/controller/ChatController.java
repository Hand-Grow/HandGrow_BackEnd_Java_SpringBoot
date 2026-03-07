package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateChatRoomRequest;
import com.handgrow.demo.dto.request.DraftContractRequest;
import com.handgrow.demo.dto.request.SendChatMessageRequest;
import com.handgrow.demo.dto.response.ChatMessageResponse;
import com.handgrow.demo.dto.response.ChatRoomResponse;
import com.handgrow.demo.dto.response.ElectronicContractResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import com.handgrow.demo.service.ChatService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> getOrCreateRoom(
            @RequestBody CreateChatRoomRequest request, Principal principal) {
        UUID accountId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(chatService.getOrCreateRoom(accountId, request));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(Principal principal) {
        UUID accountId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(chatService.getMyRooms(accountId));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable UUID roomId, Pageable pageable) {
        return ResponseEntity.ok(chatService.getMessages(roomId, pageable));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable UUID roomId, @RequestBody SendChatMessageRequest request, Principal principal) {
        UUID accountId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(chatService.sendMessage(roomId, accountId, request));
    }

    @PostMapping("/rooms/{roomId}/contract")
    public ResponseEntity<ElectronicContractResponse> draftContract(
            @PathVariable UUID roomId, @RequestBody DraftContractRequest request, Principal principal) {
        UUID accountId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(chatService.draftContract(roomId, accountId, request));
    }

    @GetMapping("/rooms/{roomId}/contract")
    public ResponseEntity<ElectronicContractResponse> getContract(@PathVariable UUID roomId) {
        return ResponseEntity.ok(chatService.getContract(roomId));
    }

    @PutMapping("/contracts/{contractId}/sign")
    public ResponseEntity<SimpleResponse> signContract(@PathVariable UUID contractId, Principal principal) {
        UUID accountId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(chatService.signContract(contractId, accountId));
    }
}
