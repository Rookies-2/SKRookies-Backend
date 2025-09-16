package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

/**
 * @author  백두현
 * @version 1.0
 * @since   2025-09-16
 * @description UserDetails를 구현한 User 엔티티.
 */
// @Data 백두현: 해당 애노테이션이 채팅방 참여자 조회 시 .toString()호출에서 LazyInitializationException 문제 발생
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "userChatRooms")
@Table(name = "users")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String username;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Email
    @NotBlank
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
    private LocalDateTime lastLoggedInAt;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // User를 직렬화할 때 userChatRooms는 포함
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    // 프로필 이미지 URL
    @Size(max = 500)
    @Column(name = "avatar_url", length = 500)  // 길이는 넉넉히
    private String avatarUrl;

    @Column(name = "verification_code", length = 500)
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
    }

    //== 비즈니스 로직 ==//
    public void approve() {
            this.status = UserStatus.ACTIVE;
            this.approvedAt = LocalDateTime.now();
    }

    public void reject() {
            this.status = UserStatus.REJECTED;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }
    //사용자 마지막 로그인 1년후 자동 비활성화
    public void updateLastLoginTime() {
        this.lastLoggedInAt = LocalDateTime.now();
    }

    public void changeUsername(String newUsername) {
        if (newUsername != null && !newUsername.isBlank()) {
            this.username = newUsername;
        }
    }

    public void changeNickname(String newNickname) {
        if (newNickname != null && !newNickname.isBlank()) {
            this.nickname = newNickname;
        }
    }

    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
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