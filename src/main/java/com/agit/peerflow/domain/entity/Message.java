package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.MessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    // 연관관계 매핑
    // Message는 한 명의 User(sender)에 속함 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    // Message는 하나의 ChatRoom에 속함 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public static Message createMessage(User sender, ChatRoom chatRoom, String content, MessageType type) {
        Message message = new Message();
        message.sender = sender;
        message.chatRoom = chatRoom;
        message.content = content;
        message.type = type;
        message.sentAt = LocalDateTime.now();

        return message;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}