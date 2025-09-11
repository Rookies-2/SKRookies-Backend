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
        // 사용자 상태 별 정책
        switch(user.getStatus()) {
            case PENDING -> throw new IllegalStateException("승인 대기 상태에서는 채팅방 입장이 제한됩니다.");
            case INACTIVE -> throw new IllegalStateException("비활성화 상태에서는 채팅방 입장이 제한됩니다.");
            case REJECTED -> throw new IllegalStateException("승인 거부된 사용자는 채팅방 입장이 제한됩니다.");
            case ACTIVE -> {
                // 활성화 상태일 때만 입장
            }
        }
        Optional<ChatParticipant> existingOpt = chatParticipantRepository.findByUserAndChatRoom(user, chatRoom);

        if (existingOpt.isPresent()) {
            ChatParticipant existing = existingOpt.get();
//            if (existing.getStatus() == ParticipantType.ACTIVE) {
//                throw new IllegalStateException("이미 참여 중인 방입니다.");
//            }
            if (existing.getStatus() == ParticipantType.BANNED) {
                throw new IllegalStateException("강퇴된 사용자는 재참여할 수 없습니다.");
            }
            if (existing.getStatus() == ParticipantType.LEFT) {
                existing.setStatus(ParticipantType.ACTIVE);
                return;
            }

            throw new IllegalStateException("현재 상태(" + existing.getStatus() + ")에서는 참여할 수 없습니다.");
        }

        // 처음 참여하는 경우
        ChatParticipant participant = ChatParticipant.create(user, chatRoom);
        // 처음 생성 시 상태를 ACTIVE로 설정
        participant.setStatus(ParticipantType.ACTIVE);
        chatParticipantRepository.save(participant);
    }

    @Override
    @Transactional
    public void leaveRoom(User user, ChatRoom chatRoom) {
        chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .ifPresent(chatParticipantRepository::delete);
    }

    @Override
    public List<ChatParticipant> getParticipants(ChatRoom chatRoom) {
        return chatParticipantRepository.findByChatRoom(chatRoom);
    }
}