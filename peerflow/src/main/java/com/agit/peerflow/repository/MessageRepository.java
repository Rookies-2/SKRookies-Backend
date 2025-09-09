package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지 시간순 조회
    List<Message> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);
}
