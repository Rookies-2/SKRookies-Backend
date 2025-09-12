package com.agit.peerflow.dto.chatroom;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.enums.ChatRoomType;

import java.time.format.DateTimeFormatter;

public record ChatRoomResponseDTO(
        Long id,
        String roomName,
        ChatRoomType type,
        int participantCount,
        long unreadCount, // 읽지 않은 메시지 수
        String date
) {
    public static ChatRoomResponseDTO from(ChatRoom room, long unreadCount, String date) {
        return new ChatRoomResponseDTO(
                room.getId(),
                room.getRoomName(),
                room.getType(),
                room.getUserChatRooms().size(),
                unreadCount,
                date
        );
    }
}

