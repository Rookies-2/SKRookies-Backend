package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.ChatRoomType;
import com.agit.peerflow.domain.enums.HistoryType;
import com.agit.peerflow.domain.enums.MessageType;
import com.agit.peerflow.dto.message.ChatMessageDTO;
import com.agit.peerflow.dto.message.ReadMessageRequestDTO;
import com.agit.peerflow.dto.message.SendMessageRequestDTO;
import com.agit.peerflow.service.ChatRoomService;
import com.agit.peerflow.service.HistoryService;
import com.agit.peerflow.service.MessageService;
import com.agit.peerflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * @author    백두현
 * @version   1.0.0
 * @since     2025-09-08
 * @description
 * - STOMP 기반 실시간 메시지 처리 컨트롤러
 * - 메시지 수신 및 브로드캐스트
 */
@Tag(name = "Chat Message API (WebSocket)", description = "STOMP 프로토콜을 이용한 실시간 메시지 처리")
@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final HistoryService historyService;

    @Operation(summary = "채팅 메시지 발신",
            description = "클라이언트가 서버로 메시지를 보냅니다. (STOMP: /app/chat/rooms/{roomId}) " +
                    "서버는 이 메시지를 /topic/chat/rooms/{roomId}를 구독 중인 모든 클라이언트에게 브로드캐스트합니다. " +
                    "또한 메시지를 /queue/messages/{roomId}를 구독 중인 클라이언트에게 private 메시지를 전송합니다." +
                    "**Swagger UI에서는 직접 테스트할 수 없습니다.**")
    @MessageMapping("/chat/rooms/{roomId}")
    public void handleMessage(@DestinationVariable Long roomId,
                              @Payload @Valid SendMessageRequestDTO dto,
                              Principal principal) {
        if (principal == null) {
            // 인증되지 않은 사용자는 메시지 전송 불가
            throw new IllegalStateException("인증된 사용자만 메시지를 전송할 수 있습니다.");
        }

        Authentication auth = (Authentication) principal;
        User user = (User) auth.getPrincipal();
        String email = user.getEmail();
        User sender = userService.getMyInfo(email);
        ChatRoom room = chatRoomService.getRoomById(roomId);

        if (sender == null) {
            throw new IllegalStateException("사용자 정보를 찾을 수 없습니다: " + user.getUsername());
        }

        User receiver = null;
        if (room.getType() == ChatRoomType.ONE_TO_ONE) {
            if (dto.receiverId() == null || dto.receiverId().isBlank()) {
                throw new IllegalArgumentException("1:1 채팅에서는 receiverId가 반드시 필요합니다.");
            }
            receiver = userService.getById(dto.receiverId());
        }

        Message saved = null;
        if(dto.type() == MessageType.TEXT) {
            saved = messageService.sendMessage(room, sender, receiver, dto.content(), MessageType.TEXT);
        } else if(dto.type() == MessageType.IMAGE) {
            saved = messageService.sendMessage(room, sender, receiver, dto.fileUrl(), MessageType.IMAGE);
        } else if(dto.type() == MessageType.FILE) {
            saved = messageService.sendMessage(room, sender, receiver, dto.fileUrl(), MessageType.FILE);
        }

        if(room.getType() == ChatRoomType.GROUP) {
            ChatMessageDTO response = ChatMessageDTO.fromGroup(saved);
            messageService.broadcastMessage(roomId, response);

            String content = ("새로운 메시지가 왔습니다.");
        } else if (room.getType() == ChatRoomType.ONE_TO_ONE) {
            ChatMessageDTO response = ChatMessageDTO.fromOneToOne(saved);
            // 수신자, 송신자에게 전송
            messageService.privateMessage(response.senderId(), response);
            messageService.privateMessage(response.receiverId(), response);
        }

        // 알림 history 저장
        historyService.createHistory(user, "새로운 메시지가 왔습니다.", "", HistoryType.MESSAGE);
    }

    @Operation(summary = "유저 별 채팅창 읽은 채팅 메시지 표시",
               description = "클라이언트는 채팅방에 들어왔을 때 이 엔드포인트로 마지막 메시지 ID를 보냅니다."
    )
    @MessageMapping("/chat/rooms/{roomId}/read")
    public void markAsRead(@DestinationVariable Long roomId,
                           Principal principal,
                           @Payload ReadMessageRequestDTO requestDTO) {
        String username = principal.getName();
        if (username.isEmpty()) {
            // 인증되지 않은 사용자는 메시지 마킹 표시 불가
            throw new IllegalStateException("인증된 사용자만 읽은 메시지를 볼 수 있습니다.");
        }

        messageService.markAsRead(roomId, username, requestDTO.lastMessageId());
    }
}
