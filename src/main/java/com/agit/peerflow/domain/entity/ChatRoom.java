package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType type;

    private ChatRoom(String roomName, ChatRoomType type) {
        this.roomName = roomName;
        this.type = type;
    }

    public static ChatRoom create(String roomName, ChatRoomType type) {
        return new ChatRoom(roomName, type);
    }
}