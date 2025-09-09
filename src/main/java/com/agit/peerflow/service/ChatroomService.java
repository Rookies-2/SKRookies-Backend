package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.enums.ChatRoomType;
import com.agit.peerflow.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author    백두현
 * @version   1.0.0
 * @since     2025-09-08
 * @description
 * 채팅방 생성 및 조회를 담당하는 서비스 클래스
 *  - 채팅방 생성
 *  - 단일 채팅방 조회
 *  - 전체 채팅방 목록 조회
 *  - 기본 트랜잭션은 읽기 전용, 생성 시에는 명시적 쓰기 트랜잭션 적용
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatroomService {
    private final ChatRoomRepository chatRoomRepository;

    // 방 생성
    @Transactional
    public ChatRoom createRoom(String roomName, ChatRoomType type) {
        ChatRoom room = ChatRoom.createChatRoom(roomName, type);
        return chatRoomRepository.save(room);
    }

    // 채팅방 검색
    public ChatRoom getRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
    }

    // 모든 방 검색
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }
}
