package com.agit.peerflow.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    // 생성 시점에 User와 ChatRoom을 반드시 받도록 강제
    private ChatParticipant(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
    }

    public static ChatParticipant create(User user, ChatRoom chatRoom) {
        return new ChatParticipant(user, chatRoom);
    }
}