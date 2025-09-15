package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지 시간순 조회
    @Query("""
    SELECT m
    FROM Message m
    JOIN FETCH m.sender
    WHERE m.chatRoom = :chatRoom
    ORDER BY m.sentAt ASC
""")
    List<Message> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);

    @Query("""
    SELECT m
    FROM Message m
    JOIN FETCH m.sender
    WHERE m.chatRoom.id = :roomId
    ORDER BY m.sentAt ASC
""")
    List<Message> findByChatRoomWithSender(@Param("roomId") Long roomId);

    // 채팅방의 메시지 삭제
    void deleteByChatRoom(ChatRoom chatRoom);

    // 특정 채팅방에서 특정 메시지 ID 이후의 메시지 개수
    long countByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long messageId);

    // 채팅방에서 가장 마지막에 생성된 메시지의 시간
    Optional<Message> findTopByChatRoomIdOrderBySentAtDesc(Long chatRoomId);

    @Query("""
    SELECT m
    FROM Message m
    JOIN FETCH m.sender
    WHERE m.id = :id
""")
    Optional<Message> findByIdWithSender(@Param("id") Long id);
}
