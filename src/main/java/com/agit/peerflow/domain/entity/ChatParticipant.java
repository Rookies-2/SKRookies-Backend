package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.ParticipantType;
import com.agit.peerflow.domain.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author  백두현
 * @version 1.0
 * @since   2025-09-16
 * @description 채팅방 참여자 정보를 저장하는 엔티티.
 *              사용자(User), 채팅방(ChatRoom), 참여 상태(status), 마지막 읽은 메시지 ID,
 *              퇴장 시각(leftAt), 마지막 활동 시각(updatedAt) 등을 관리한다.
 */
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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipantType status;

    @Min(0)
    @Column(name = "last_read_message_id", nullable = false)
    private Long lastReadMessageId = 0L;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
        this.updatedAt = lastMessageTime != null ? lastMessageTime : LocalDateTime.now();
    }
}