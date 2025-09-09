package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.message.ChatMessageDTO;
import com.agit.peerflow.dto.chatroom.ChatRoomResponseDTO;
import com.agit.peerflow.dto.chatroom.CreateRoomRequestDTO;
import com.agit.peerflow.dto.user.UserResponseDTO;
import com.agit.peerflow.service.ChatParticipantService;
import com.agit.peerflow.service.ChatroomService;
import com.agit.peerflow.service.MessageService;
import com.agit.peerflow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * @author    백두현
 * @version   1.0.0
 * @since     2025-09-08
 * @description
 * - 채팅방 관련 REST API 컨트롤러
 * - 채팅방 생성, 참여, 나가기
 * - 참여자 목록 및 메시지 조회
 */
@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final MessageService messageService;
    private final UserService userService;

    // 채팅방 생성
    @PostMapping
    public ResponseEntity<ChatRoomResponseDTO> createRoom(@RequestBody @Valid CreateRoomRequestDTO request) {
        ChatRoom room = chatroomService.createRoom(request.roomName(), request.type());
        return ResponseEntity.ok(ChatRoomResponseDTO.from(room));
    }

    // 채팅방 참여
    @PostMapping("/{roomId}/join")
    public ResponseEntity<Void> joinRoom(@PathVariable Long roomId, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        ChatRoom room = chatroomService.getRoom(roomId);
        chatParticipantService.joinRoom(user, room);
        return ResponseEntity.ok().build();
    }

    // 채팅방 나가기
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable Long roomId, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        ChatRoom room = chatroomService.getRoom(roomId);
        chatParticipantService.leaveRoom(user, room);
        return ResponseEntity.ok().build();
    }

    // 참여자 목록 조회
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<List<UserResponseDTO>> getParticipants(@PathVariable Long roomId) {
        ChatRoom room = chatroomService.getRoom(roomId);
        List<ChatParticipant> participants = chatParticipantService.getParticipants(room);
        List<UserResponseDTO> response = participants.stream()
                .map(p -> UserResponseDTO.from(p.getUser()))
                .toList();
        return ResponseEntity.ok(response);
    }

    // 메시지 목록 조회
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable Long roomId) {
        ChatRoom room = chatroomService.getRoom(roomId);
        List<Message> messages = messageService.getMessages(room);
        List<ChatMessageDTO> response = messages.stream()
                .map(ChatMessageDTO::from)
                .toList();
        return ResponseEntity.ok(response);
    }

}
