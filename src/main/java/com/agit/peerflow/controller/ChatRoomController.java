package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.message.ChatMessageDTO;
import com.agit.peerflow.dto.chatroom.ChatRoomResponseDTO;
import com.agit.peerflow.dto.chatroom.CreateRoomRequestDTO;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.service.ChatParticipantService;
import com.agit.peerflow.service.ChatRoomService;
import com.agit.peerflow.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author    백두현
 * @version   1.1
 * @since     2025-09-09
 * @description 채팅방 관련 REST API 컨트롤러 (인증 방식 및 로직 수정)
 */
@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatParticipantService chatParticipantService;
    private final MessageService messageService;

    // 채팅방 생성
    @PostMapping
    public ResponseEntity<ChatRoomResponseDTO> createRoom(
            @RequestBody @Valid CreateRoomRequestDTO request,
            @AuthenticationPrincipal User user) {
        ChatRoom room = chatRoomService.createRoom(request, user);
        return ResponseEntity.ok(ChatRoomResponseDTO.from(room));
    }

    // 채팅방 참여 (초대)
    @PostMapping("/{roomId}/participants/{userId}")
    public ResponseEntity<Void> inviteUserToRoom(
            @PathVariable Long roomId,
            @PathVariable Long userId) {
        // TODO: ChatRoomService에 사용자 초대 메소드를 만들고 호출
        // chatRoomService.inviteUserToRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    // 채팅방 나가기
    @DeleteMapping("/{roomId}/participants")
    public ResponseEntity<Void> leaveRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) {
        ChatRoom room = chatRoomService.getRoomById(roomId);
        chatParticipantService.leaveRoom(user, room);
        return ResponseEntity.ok().build();
    }

    // 참여자 목록 조회
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<List<UserDTO.Response>> getParticipants(@PathVariable Long roomId) {
        ChatRoom room = chatRoomService.getRoomById(roomId);
        List<ChatParticipant> participants = chatParticipantService.getParticipants(room);
        List<UserDTO.Response> response = participants.stream()
                .map(p -> UserDTO.Response.fromEntity(p.getUser()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 메시지 목록 조회
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable Long roomId) {
        ChatRoom room = chatRoomService.getRoomById(roomId);
        List<Message> messages = messageService.getMessages(room);
        List<ChatMessageDTO> response = messages.stream()
                .map(ChatMessageDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}