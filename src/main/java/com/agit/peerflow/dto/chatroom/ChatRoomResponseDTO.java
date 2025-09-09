package com.agit.peerflow.dto.chatroom;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.enums.ChatRoomType;

public record ChatRoomResponseDTO(
        Long id,
        String roomName,
        ChatRoomType type
) {
    public static ChatRoomResponseDTO from(ChatRoom room) {
        return new ChatRoomResponseDTO(room.getId(), room.getRoomName(), room.getType());
    }
}

