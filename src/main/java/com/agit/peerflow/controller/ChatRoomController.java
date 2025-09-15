package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.chatroom.ChatRoomResponseDTO;
import com.agit.peerflow.dto.chatroom.CreateRoomRequestDTO;
import com.agit.peerflow.dto.message.ChatMessageDTO;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.service.ChatParticipantService;
import com.agit.peerflow.service.ChatRoomService;
import com.agit.peerflow.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author    백두현
 * @version   1.2
 * @since     2025-09-10
 * @description 채팅방 관련 REST API 컨트롤러
 */
@Tag(name = "Chat Room API", description = "채팅방 생성, 조회, 참여/나가기 관련 API")
@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatParticipantService chatParticipantService;
    private final MessageService messageService;

    @Operation(summary = "새 채팅방 생성", description = "새로운 그룹 채팅방 또는 1:1 채팅방을 생성합니다. 생성자가 첫 참여자로 자동 등록됩니다.")
    @PostMapping
    public ResponseEntity<ChatRoomResponseDTO> createRoom(
            @RequestBody @Valid CreateRoomRequestDTO request,
            @AuthenticationPrincipal User user) {
        ChatRoom room = chatRoomService.createRoom(request, user);
        return ResponseEntity.ok(ChatRoomResponseDTO.from(room,0, String.valueOf(LocalDateTime.now())));
    }

    @Operation(summary = "채팅방에 사용자 초대", description = "기존 그룹 채팅방에 ID에 해당하는 다른 사용자를 초대합니다.")
    @PostMapping("/{roomId}/participants/{userId}")
    public ResponseEntity<Void> inviteUserToRoom(
            @PathVariable Long roomId,
            @PathVariable Long userId) {
        //chatRoomService.inviteUserToRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary= "채팅방 들어가기", description = "현재 로그인된 사용자가 참여중인 채팅방에서 참여합니다.")
    @PostMapping("/{roomId}/join")
    public ResponseEntity<Void> joinRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) {
          ChatRoom room = chatRoomService.getRoomById(roomId);
          chatParticipantService.joinRoom(user, room);
          return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 나가기", description = "현재 로그인된 사용자가 참여중인 채팅방에서 나갑니다.")
    @PutMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) {
        ChatRoom room = chatRoomService.getRoomById(roomId);
        chatParticipantService.leaveRoom(user, room);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 참여자 목록 조회", description = "특정 채팅방에 참여하고 있는 모든 사용자 목록을 조회합니다.")
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<List<UserDTO.Response>> getParticipants(@PathVariable Long roomId) {
        ChatRoom room = chatRoomService.getRoomById(roomId);
        List<ChatParticipant> participants = chatParticipantService.getParticipants(room);
        List<UserDTO.Response> response = participants.stream()
                .map(p -> UserDTO.Response.fromEntity(p.getUser()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채팅방 이전 메시지 목록 조회", description = "특정 채팅방의 이전 대화 내용을 조회합니다.")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable Long roomId) {
        ChatRoom room = chatRoomService.getRoomById(roomId);
        List<Message> messages = messageService.getMessages(room);
        List<ChatMessageDTO> response = messages.stream()
                .map(ChatMessageDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 방 조회", description = "사용자가 로그인 시 사용자의 모든 방을 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<ChatRoomResponseDTO>> findAllChatRooms(@AuthenticationPrincipal User user) {
        String userName = user.getUsername();
        List<ChatRoomResponseDTO> dto = chatRoomService.findUnreadMessagesPerRoom(userName);
        return ResponseEntity.ok(dto);
    }
}