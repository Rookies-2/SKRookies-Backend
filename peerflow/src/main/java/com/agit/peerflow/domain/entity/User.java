package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users") // 'user'는 여러 DB에서 예약어일 수 있으므로 'users' 사용을 권장합니다.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    // User는 여러개의 Message를 보냄 (1:N)
    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages = new ArrayList<>();

    // User는 여러개의 ChatRoom에 참여 (N:M, 중간 테이블 ChatParticipant)
    @OneToMany(mappedBy = "user")
    private List<ChatParticipant> chatParticipants = new ArrayList<>();

    public static User createUser(String username, String password, String nickname, UserRole role, UserStatus status) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.nickname = nickname;
        user.role = role;
        user.status = status;
        return user;
    }

    public void addChatParticipant(ChatParticipant participant) {
        chatParticipants.add(participant);
        participant.setUser(this);
    }

    public void changeStatus(UserStatus status) {
        if(this.status == UserStatus.PENDING) {
            throw new IllegalStateException("정지된 사용자는 상태를 변경할 수 없습니다.");
        }
        this.status = status;
    }
}
