package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지 시간순 조회
    List<Message> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);

    // 채팅방의 메시지 삭제
    void deleteByChatRoom(ChatRoom chatRoom);

    // 특정 채팅방에서 특정 메시지 ID 이후의 메시지 개수
    long countByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long messageId);

    // 채팅방에서 가장 마지막에 생성된 메시지의 시간
    Optional<Message> findTopByChatRoomIdOrderBySentAtDesc(Long chatRoomId);
}
