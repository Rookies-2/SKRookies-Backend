package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.chatroom.ChatRoomResponseDTO;
import com.agit.peerflow.dto.chatroom.CreateRoomRequestDTO;

import java.util.List;

public interface ChatRoomService {

    // 새 채팅방 생성
    ChatRoom createRoom(CreateRoomRequestDTO request, User creator);

    // ID로 채팅방 조회
    ChatRoom getRoomById(Long roomId);

    // 내가 참여중인 모든 채팅방 목록 조회
    List<ChatRoom> getMyChatRooms(User user);

    // (경고: N+1) 안 읽은 메시지 수를 포함한 내 채팅방 목록
    List<ChatRoomResponseDTO> findUnreadMessagesPerRoomIncludingGroups(String userName);

    // (경고: N+1) 모든 채팅방 리스트 조회
    List<ChatRoomResponseDTO> findAllChatRooms();

    // 채팅방 및 관련 데이터 삭제
    void deleteRoom(ChatRoom chatRoom);
}