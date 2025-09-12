package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.ParticipantType;
import com.agit.peerflow.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    // 사용자가 마지막으로 읽은 메시지의 ID
    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    // 사용자가 채팅방을 나간 시각
    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 생성 시점에 User와 ChatRoom을 반드시 받도록 강제
    private ChatParticipant(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
    }

    public static ChatParticipant create(User user, ChatRoom chatRoom) {
        ChatParticipant participant = new ChatParticipant(user, chatRoom);
        participant.status = ParticipantType.ACTIVE;
        participant.lastReadMessageId = 0L;

        return participant;
    }

    public void setStatus(ParticipantType type) {
        if (this.status == ParticipantType.BANNED && type == ParticipantType.ACTIVE) {
            throw new IllegalStateException("강퇴된 사용자는 재참여할 수 없습니다.");
        }
        this.status = type;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }

    public void updateLastReadMessageId(Long messageId) {
        this.lastReadMessageId = messageId;
    }

    public void updateLastMessageTime(LocalDateTime lastMessageTime) {
        this.updatedAt = LocalDateTime.now();
    }
}