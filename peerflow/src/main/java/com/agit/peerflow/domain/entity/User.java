package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
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

    @Column(unique = true, nullable = false, length = 100)
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

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

    public static User createUser(String username, String password, String nickname, String email, UserRole role, UserStatus status) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.nickname = nickname;
        user.email = email;
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

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 비어있을 수 없습니다.");
        }
        this.username = username;
    }

    public void setNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        this.nickname = nickname;
    }

    public void setPassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        }
        this.password = encodedPassword;
    }
}