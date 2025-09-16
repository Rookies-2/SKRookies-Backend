package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.ChatRoomType;
import com.agit.peerflow.domain.enums.ParticipantType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author  백두현
 * @version 1.0
 * @since   2025-09-16
 * @description 채팅방 참여자(ChatParticipant) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository.
 *              채팅방별 참여자 조회, 유저-채팅방 매핑 검색, 참여자 삭제, 상태별 조회 등의 기능을 제공한다.
 */
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    // 특정 방의 모든 참여자 (User 즉시 로딩)
    @Query("""
        SELECT cp
        FROM ChatParticipant cp
        JOIN FETCH cp.user
        WHERE cp.chatRoom = :chatRoom
          AND cp.status = :status
    """)
    List<ChatParticipant> findByChatRoomWithUser(@Param("chatRoom") ChatRoom chatRoom,
                                                 @Param("status") ParticipantType status);

    // 특정 유저가 특정 방에 있는지 확인 (비관적 락)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.user = :user AND cp.chatRoom = :chatRoom")
    Optional<ChatParticipant> findByUserAndChatRoom(User user, ChatRoom chatRoom);

    // 특정 유저가 참여중인 모든 방 (채팅방 즉시 로딩)
    @EntityGraph(attributePaths = "chatRoom")
    List<ChatParticipant> findByUser(User user);

    // 채팅방의 참여자 삭제
    void deleteByChatRoom(ChatRoom chatRoom);

    List<ChatParticipant> findAllByUserUsernameAndStatus(String userName, ParticipantType status);

    Optional<ChatParticipant> findByChatRoomIdAndUserUsername(Long roomId, String userName);
}
