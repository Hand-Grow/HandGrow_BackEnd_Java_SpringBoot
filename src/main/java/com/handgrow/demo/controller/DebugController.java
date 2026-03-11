package com.handgrow.demo.controller;

import com.handgrow.demo.document.MongoChatMessage;
import com.handgrow.demo.document.MongoChatRoom;
import com.handgrow.demo.repository.MongoChatMessageRepository;
import com.handgrow.demo.repository.MongoChatRoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/debug")
@RequiredArgsConstructor
public class DebugController {

    private final MongoChatRoomRepository chatRoomRepository;
    private final MongoChatMessageRepository chatMessageRepository;

    @GetMapping("/rooms")
    public List<MongoChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }

    @GetMapping("/messages")
    public List<MongoChatMessage> getAllMessages() {
        return chatMessageRepository.findAll();
    }
}
