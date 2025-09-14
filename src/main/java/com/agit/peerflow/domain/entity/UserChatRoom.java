package com.agit.peerflow.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author  백두현
 * @version 1.0
 * @since   2025-09-10
 * @description User ↔ ChatRoom의 다대다 관계인 중간 엔티티.
 * - 전체 방 조회
 * TODO 입장 시간, 역할, 상태 같은 부가 정보도 함께 관리
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name= "user_chatroom")
public class UserChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    private LocalDateTime joinedAt;
    private boolean muted; // 알림 끔 여부
    private boolean pinned; // 상단 고정 여부

    private UserChatRoom(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.joinedAt = LocalDateTime.now();
        this.muted = false;
        this.pinned = false;
    }

    public static UserChatRoom create(User user, ChatRoom chatRoom) {
        return new UserChatRoom(user, chatRoom);
    }

    public void toggleMute() {
        this.muted = !this.muted;
    }

    public void togglePinned() {
        this.pinned = !this.pinned;
    }

    public void setMute(boolean mute) {
        this.muted = mute;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
