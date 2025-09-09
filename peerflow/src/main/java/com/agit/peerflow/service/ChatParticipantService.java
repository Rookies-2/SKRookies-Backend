package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author    백두현
 * @version   1.0.0
 * @since     2025-09-08
 * @description
 * - 채팅방 참여자 관련 비즈니스 로직을 담당하는 서비스 클래스
 * - 채팅방 참여 처리
 * - 채팅방 나가기 처리
 * - 특정 채팅방의 참여자 목록 조회
 * - 기본 트랜잭션은 읽기 전용이며, 참여/나가기 시에는 명시적 쓰기 트랜잭션 적용
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatParticipantService {
    private final ChatParticipantRepository chatParticipantRepository;

    // 방 참여
    @Transactional
    public ChatParticipant joinRoom(User user, ChatRoom chatRoom) {
        chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .ifPresent(cp->{ throw new IllegalStateException("이미 참여 중인 방입니다."); });

        ChatParticipant participant = ChatParticipant.createChatParticipant(user, chatRoom);
        user.addChatParticipant(participant);
        chatRoom.addParticipant(participant);

        return chatParticipantRepository.save(participant);
    }

    // 방 나가기
    @Transactional
    public void leaveRoom(User user, ChatRoom chatRoom) {
        chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .ifPresent(chatParticipantRepository::delete);
    }

    // 참여자 정보 가져오기
    public List<ChatParticipant> getParticipants(ChatRoom chatRoom) {
        return chatParticipantRepository.findByChatRoom(chatRoom);
    }
}
