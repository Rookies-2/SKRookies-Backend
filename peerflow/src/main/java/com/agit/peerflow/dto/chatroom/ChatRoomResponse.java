package com.agit.peerflow.dto.chatroom;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.enums.ChatRoomType;

public record ChatRoomResponse(
        Long id,
        String roomName,
        ChatRoomType type
) {
    public static ChatRoomResponse from(ChatRoom room) {
        return new ChatRoomResponse(room.getId(), room.getRoomName(), room.getType());
    }
}

