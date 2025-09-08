package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.MessageType;
import com.agit.peerflow.dto.message.ChatMessageDTO;
import com.agit.peerflow.dto.message.SendMessageRequestDTO;
import com.agit.peerflow.service.ChatroomService;
import com.agit.peerflow.service.MessageService;
import com.agit.peerflow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * @author    백두현
 * @version   1.0.0
 * @since     2025-09-08
 * @description
 * - STOMP 기반 실시간 메시지 처리 컨트롤러
 * - 메시지 수신 및 브로드캐스트
 */
@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final MessageService messageService;
    private final ChatroomService chatroomService;
    private final UserService userService;

    @MessageMapping("/rooms/{roomId}")
    public void handleMessage(@DestinationVariable Long roomId,
                              @Payload @Valid SendMessageRequestDTO dto,
                              Principal principal) {
        if (principal == null) {
            // 인증되지 않은 사용자는 메시지 전송 불가
            throw new IllegalStateException("인증된 사용자만 메시지를 전송할 수 있습니다.");
        }

        String username = principal.getName();
        User sender = userService.getByUsername(username);
        if (sender == null) {
            throw new IllegalStateException("사용자 정보를 찾을 수 없습니다: " + username);
        }
        ChatRoom room = chatroomService.getRoom(roomId);

        Message saved = messageService.sendMessage(room, sender, dto.content(), MessageType.TEXT);
        ChatMessageDTO response = ChatMessageDTO.from(saved);

        messageService.broadcastMessage(roomId, response);

    }
}
