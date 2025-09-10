package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    @Query("SELECT ucr FROM ChatRoom ucr")
    List<ChatRoom> findAllChatRooms();
}
