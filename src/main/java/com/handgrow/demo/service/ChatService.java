package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateChatRoomRequest;
import com.handgrow.demo.dto.request.SendChatMessageRequest;
import com.handgrow.demo.dto.response.ChatMessageResponse;
import com.handgrow.demo.dto.response.ChatRoomResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ChatService {
    ChatRoomResponse getOrCreateRoom(UUID accountId, CreateChatRoomRequest request);

    List<ChatRoomResponse> getMyRooms(UUID accountId);

    List<ChatMessageResponse> getMessages(String roomId, UUID accountId, Pageable pageable);

    ChatMessageResponse sendMessage(String roomId, UUID accountId, SendChatMessageRequest request);
}
