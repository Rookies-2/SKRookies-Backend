package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.ChatRoomType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  백두현
 * @version 1.0
 * @since   2025-09-16
 * @description 채팅방(ChatRoom) 정보를 저장하는 엔티티.
 *              채팅방 이름(roomName), 유형(type), 생성일시(createdAt)과
 *              유저-채팅방 매핑(UserChatRoom) 관계를 관리하며,
 *              유저를 채팅방에 추가하는 기능을 제공한다.
 */
@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String roomName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatRoomType type;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 유저-채팅방 중간 엔티티, 채팅방의 유저 추가 메서드
    public void addUser(User user) {
        boolean alreadyExists = userChatRooms.stream()
                .anyMatch(link -> link.getUser().equals(user));
        if (!alreadyExists) {
            UserChatRoom link = UserChatRoom.create(user, this);
            userChatRooms.add(link);
        }
    }

    private ChatRoom(String roomName, ChatRoomType type) {
        this.roomName = roomName;
        this.type = type;
    }

    public static ChatRoom create(String roomName, ChatRoomType type) {
        return new ChatRoom(roomName, type);
    }
}