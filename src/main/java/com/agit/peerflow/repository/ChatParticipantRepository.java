package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    // 특정 방의 모든 참여자
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    // 특정 유저가 특정 방에 있는지 확인
    Optional<ChatParticipant> findByUserAndChatRoom(User user, ChatRoom chatRoom);

    // 특정 유저가 참여중인 모든 방
    List<ChatParticipant> findByUser(User user);
}
