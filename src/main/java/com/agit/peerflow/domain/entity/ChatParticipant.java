package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.ParticipantType;
import com.agit.peerflow.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "chat_participant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "chat_room_id"})
)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantType status;

    // 생성 시점에 User와 ChatRoom을 반드시 받도록 강제
    private ChatParticipant(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
    }

    public static ChatParticipant create(User user, ChatRoom chatRoom) {
        ChatParticipant participant = new ChatParticipant(user, chatRoom);
        participant.status = ParticipantType.ACTIVE;

        return participant;
    }


}