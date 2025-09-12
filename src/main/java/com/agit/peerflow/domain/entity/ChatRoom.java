package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();


    // 유저-채팅방 중간 엔티티, 채팅방의 유저 추가 메서드
    public void addUser(User user) {
        UserChatRoom link = UserChatRoom.create(user, this);
        userChatRooms.add(link);
    }

    private ChatRoom(String roomName, ChatRoomType type) {
        this.roomName = roomName;
        this.type = type;
    }

    public static ChatRoom create(String roomName, ChatRoomType type) {
        return new ChatRoom(roomName, type);
    }
}