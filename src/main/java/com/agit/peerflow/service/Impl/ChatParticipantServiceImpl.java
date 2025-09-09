package com.agit.peerflow.service.Impl;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.repository.ChatParticipantRepository;
import com.agit.peerflow.service.ChatParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author    백두현
 * @version   1.1
 * @since     2025-09-09
 * @description 채팅방 참여자 관련 비즈니스 로직 구현체 (수정)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatParticipantServiceImpl implements ChatParticipantService {
    private final ChatParticipantRepository chatParticipantRepository;

    @Override
    @Transactional
    public void joinRoom(User user, ChatRoom chatRoom) {
        chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .ifPresent(cp -> { throw new IllegalStateException("이미 참여 중인 방입니다."); });

        // User나 ChatRoom을 직접 수정하지 않고, ChatParticipant만 생성하여 저장합니다.
        ChatParticipant participant = ChatParticipant.create(user, chatRoom);
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