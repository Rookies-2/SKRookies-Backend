package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.enums.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 방 이름으로 검색
    List<ChatRoom> findByRoomNameContaining(String keyword);
    // 타입별 방 목록
    List<ChatRoom> findByType(ChatRoomType type);
}
