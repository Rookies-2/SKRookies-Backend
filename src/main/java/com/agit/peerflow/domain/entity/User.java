package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
// @Data 백두현: 해당 애노테이션이 채팅방 참여자 조회 시 .toString()호출에서 LazyInitializationException 문제 발생
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "userChatRooms")
@Table(name = "users")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime approvedAt;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // User를 직렬화할 때 userChatRooms는 포함
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    // 비밀번호 재설정 인증번호 관련
    private String passwordResetToken;

    // 프로필 이미지 URL
    @Column(name = "avatar_url", length = 500)  // 길이는 넉넉히
    private String avatarUrl;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expiration")
    private LocalDateTime verificationCodeExpiration;

    @Builder
    private User(String username, String password, String nickname, String email, UserRole role, UserStatus status, String avatarUrl) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.status = (status != null) ? status : UserStatus.PENDING;
        this.avatarUrl = avatarUrl;
        //this.createdAt = LocalDateTime.now();
    }

    // 유저-채팅방 중간 엔티티, 채팅방 추가 메서드
    public void addChatRoom(ChatRoom chatRoom) {
        UserChatRoom link = UserChatRoom.create(this, chatRoom);
        userChatRooms.add(link);
    }
    //== 비즈니스 로직 ==//
    public void approve() {
        if (this.status == UserStatus.PENDING) {
            this.status = UserStatus.ACTIVE;
            this.approvedAt = LocalDateTime.now();
        }
    }

    public void reject() {
        if (this.status == UserStatus.PENDING) {
            this.status = UserStatus.REJECTED;
        }
    }

    /**
     * 사용자 이름(username)을 변경합니다.
     * @param newUsername 새로운 사용자 이름
     */
    public void changeUsername(String newUsername) {
        if (newUsername != null && !newUsername.isBlank()) {
            this.username = newUsername;
        }
    }

    /**
     * 닉네임(nickname)을 변경합니다.
     * @param newNickname 새로운 닉네임
     */
    public void changeNickname(String newNickname) {
        if (newNickname != null && !newNickname.isBlank()) {
            this.nickname = newNickname;
        }
    }

    // 인증 코드 설정
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    // 인증 코드 만료 시간 설정
    public void setVerificationCodeExpiration(LocalDateTime verificationCodeExpiration) {
        this.verificationCodeExpiration = verificationCodeExpiration;
    }

    // 비밀번호(문자열) 설정
    public void setPassword(String password) {
        this.password = password;
    }

    // 아바타 URL 설정
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    // username 변경
    public void setUsername(String username) {
        if (username != null && !username.isBlank()) {
            this.username = username;
        }
    }

    // nickname 변경
    public void setNickname(String nickname) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
    }


    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    // --- UserDetails 구현 메소드 --- //
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //SHIN ("ROLE_" + role.name())로 변경
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override
    public String getUsername() { return username; }
    @Override
    public String getPassword() { return password; }
    @Override
    public boolean isEnabled() { return status == UserStatus.ACTIVE; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
}