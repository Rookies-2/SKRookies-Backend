package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.ChatRoomType;
import com.agit.peerflow.dto.chatroom.CreateRoomRequestDTO;
import com.agit.peerflow.repository.ChatParticipantRepository;
import com.agit.peerflow.repository.ChatRoomRepository;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    public ChatRoom createRoom(CreateRoomRequestDTO request, User creator) {
        // getter 대신 record의 접근자 메소드를 사용합니다.
        ChatRoom newRoom = ChatRoom.create(request.roomName(), request.type());
        chatRoomRepository.save(newRoom);

        ChatParticipant creatorParticipant = ChatParticipant.create(creator, newRoom);
        chatParticipantRepository.save(creatorParticipant);

        if (request.type() == ChatRoomType.ONE_TO_ONE && request.targetUserId() != null) {
            User targetUser = userRepository.findById(request.targetUserId())
                    .orElseThrow(() -> new IllegalArgumentException("상대방 사용자를 찾을 수 없습니다."));
            ChatParticipant targetParticipant = ChatParticipant.create(targetUser, newRoom);
            chatParticipantRepository.save(targetParticipant);
        }
        return newRoom;
    }

    @Transactional(readOnly = true)
    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getMyChatRooms(User user) {
        return chatParticipantRepository.findByUser(user)
                .stream()
                .map(ChatParticipant::getChatRoom)
                .collect(Collectors.toList());
    }
}