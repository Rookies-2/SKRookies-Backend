package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public static ChatParticipant createChatParticipant(User user, ChatRoom chatRoom) {
        ChatParticipant chatParticipant = new ChatParticipant();
        chatParticipant.setUser(user);
        chatParticipant.setChatRoom(chatRoom);

        return chatParticipant;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}