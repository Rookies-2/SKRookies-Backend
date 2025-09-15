package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.ParticipantType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    // 특정 방의 모든 참여자
    @Query("SELECT cp FROM ChatParticipant cp JOIN FETCH cp.user WHERE cp.chatRoom = :chatRoom")
    List<ChatParticipant> findByChatRoomWithUser(@Param("chatRoom") ChatRoom chatRoom);

    // 특정 유저가 특정 방에 있는지 확인
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락을 걸어 조회 시점에 다른 트랜잭션이 insert 못하게 방지
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.user = :user AND cp.chatRoom = :chatRoom")
    Optional<ChatParticipant> findByUserAndChatRoom(User user, ChatRoom chatRoom);

    // 특정 유저가 참여중인 모든 방
    List<ChatParticipant> findByUser(User user);

    // 채팅방의 참여자 삭제
    void deleteByChatRoom(ChatRoom chatRoom);

    List<ChatParticipant> findAllByUserUsernameAndStatus(String userName, ParticipantType status);

    Optional<ChatParticipant> findByChatRoomIdAndUserUsername(Long roomId, String userName);
}
