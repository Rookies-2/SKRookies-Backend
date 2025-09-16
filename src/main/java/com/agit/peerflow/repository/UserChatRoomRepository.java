package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.entity.UserChatRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author  백두현
 * @version 1.0
 * @since   2025-09-16
 * @description User와 ChatRoom 간의 매핑 엔티티(UserChatRoom)에 대한 데이터 접근을 담당하는 JPA Repository.
 *              채팅방 목록 조회, 사용자-채팅방 매핑 검색, 연관 엔티티(User, ChatRoom) 즉시 로딩 기능을 제공한다.
 */
public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    @Query("SELECT ucr FROM ChatRoom ucr")
    List<ChatRoom> findAllChatRooms();

    Optional<UserChatRoom> findByUserUsernameAndChatRoomId(String username, Long chatRoomId);

    @EntityGraph(attributePaths = {"user", "chatRoom"})
    Optional<UserChatRoom> findWithUserAndChatRoomById(Long id);

    @EntityGraph(attributePaths = "chatRoom")
    List<UserChatRoom> findByUserId(Long userId);

    void deleteByUser(User user);
}
