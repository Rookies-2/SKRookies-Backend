package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.enums.ChatRoomType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
/**
 * @author  백두현
 * @version 1.0
 * @since   2025-09-16
 * @description 채팅방(ChatRoom) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository.
 *              채팅방 이름 검색 및 채팅방 유형별 목록 조회 기능을 제공한다.
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 방 이름으로 검색
    List<ChatRoom> findByRoomNameContaining(String keyword);

    // 타입별 방 목록
    List<ChatRoom> findByType(ChatRoomType type);

    @EntityGraph(attributePaths = "userChatRooms")
    @Query("SELECT c FROM ChatRoom c")
    List<ChatRoom> findAllWithUserChatRooms();

}
