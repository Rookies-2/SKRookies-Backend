package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType type;

    // 연관관계 매핑
    // ChatRoom은 여러개의 Message를 포함 (1:N)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    // ChatRoom은 여러명의 User가 참여 (N:M, 중간 테이블 ChatParticipant)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    public static ChatRoom createChatRoom(String roomName, ChatRoomType type) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomName = roomName;
        chatRoom.type = type;

        return chatRoom;
    }

    public void addParticipant(ChatParticipant chatParticipant) {
        participants.add(chatParticipant);
        chatParticipant.setChatRoom(this);
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setChatRoom(this);
    }
}