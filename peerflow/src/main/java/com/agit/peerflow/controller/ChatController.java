package com.agit.peerflow.controller;

import com.agit.peerflow.dto.ChatMessage;
import com.agit.peerflow.dto.SendMessageRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/rooms/{roomId}")
    public void sendToRoom(@DestinationVariable String roomId, @Payload @Valid SendMessageRequest request, Principal principal) {
        String senderId = principal != null ? principal.getName() : "anonymous";
        ChatMessage msg = new ChatMessage(roomId, senderId, request.content(), System.currentTimeMillis());

        // 메시지 전송
        simpMessagingTemplate.convertAndSend("/topic/rooms/" + roomId, msg);
    }

    // 방 생성

    // 방 참여

    // 초대

    // 나가기

    // 참여자 목록

    // 인원수
}
