package com.agit.peerflow.service.Impl;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.ParticipantType;
import com.agit.peerflow.repository.ChatParticipantRepository;
import com.agit.peerflow.repository.ChatRoomRepository;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.ChatParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author    백두현
 * @version   1.2
 * @since     2025-09-09
 * @description 채팅방 참여자 관련 비즈니스 로직 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatParticipantServiceImpl implements ChatParticipantService {
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void joinRoom(User user, ChatRoom chatRoom) {
        // 사용자 상태 체크
        switch(user.getStatus()) {
            case PENDING -> throw new IllegalStateException("승인 대기 상태에서는 채팅방 입장이 제한됩니다.");
            case INACTIVE -> throw new IllegalStateException("비활성화 상태에서는 채팅방 입장이 제한됩니다.");
            case REJECTED -> throw new IllegalStateException("승인 거부된 사용자는 채팅방 입장이 제한됩니다.");
            case ACTIVE -> {
                // 활성화 상태일 때만 입장
            }
        }
        ChatParticipant participant = chatParticipantRepository
                .findByUserAndChatRoom(user, chatRoom)
                .orElseGet(() -> ChatParticipant.create(user, chatRoom));

        if (participant.getStatus() == ParticipantType.BANNED) {
            throw new IllegalStateException("강퇴된 사용자는 재참여할 수 없습니다.");
        }

        // 처음 생성 시 상태를 ACTIVE로 설정
        participant.setStatus(ParticipantType.ACTIVE);
        chatParticipantRepository.save(participant);
    }

    @Override
    @Transactional
    public void leaveRoom(User user, ChatRoom chatRoom) {
        chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .ifPresent(chatParticipant -> {
                   if(chatParticipant.getStatus() != ParticipantType.BANNED) {
                       chatParticipant.setStatus(ParticipantType.LEFT);
                   }
                });
    }

    @Override
    public List<ChatParticipant> getParticipants(ChatRoom chatRoom) {
        return chatParticipantRepository.findByChatRoomWithUser(chatRoom);
    }
}