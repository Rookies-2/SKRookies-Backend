package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String userName;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickName;

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

    @Builder
    private User(String userName, String password, String nickName, String email, UserRole role, UserStatus status) {
        this.userName = userName;
        this.password = password;
        this.nickName = nickName;
        this.email = email;
        this.role = role;
        this.status = (status != null) ? status : UserStatus.PENDING;
        this.createdAt = LocalDateTime.now();
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

    public void updateProfile(String newUserName, String newNickName) {
        if (newUserName != null && !newUserName.isBlank()) this.userName = newUserName;
        if (newNickName != null && !newNickName.isBlank()) this.nickName = newNickName;
    }

    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    // --- UserDetails 구현 메소드 --- //
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(new SimpleGrantedAuthority(role.name())); }
    @Override
    public String getUsername() { return userName; }
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