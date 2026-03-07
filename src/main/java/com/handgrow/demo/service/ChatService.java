package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateChatRoomRequest;
import com.handgrow.demo.dto.request.DraftContractRequest;
import com.handgrow.demo.dto.request.SendChatMessageRequest;
import com.handgrow.demo.dto.response.ChatMessageResponse;
import com.handgrow.demo.dto.response.ChatRoomResponse;
import com.handgrow.demo.dto.response.ElectronicContractResponse;
import com.handgrow.demo.dto.response.SimpleResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ChatService {
    ChatRoomResponse getOrCreateRoom(UUID accountId, CreateChatRoomRequest request);

    List<ChatRoomResponse> getMyRooms(UUID accountId);

    List<ChatMessageResponse> getMessages(UUID roomId, Pageable pageable);

    ChatMessageResponse sendMessage(UUID roomId, UUID accountId, SendChatMessageRequest request);

    ElectronicContractResponse draftContract(UUID roomId, UUID accountId, DraftContractRequest request);

    ElectronicContractResponse getContract(UUID roomId);

    SimpleResponse signContract(UUID contractId, UUID accountId);
}
