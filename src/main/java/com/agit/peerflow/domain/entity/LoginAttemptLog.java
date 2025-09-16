package com.agit.peerflow.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author  신명철
 * @version 1.0
 * @since   2025-09-16
 * @description 로그인 시도 이력을 저장하는 엔티티.
 *              사용자, 이메일, IP, 디바이스, 성공 여부, 시도 횟수, AI 차단 여부, 특징 데이터(JSON), 생성일시 등을 기록한다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "login_attempt_log")
@EntityListeners(AuditingEntityListener.class)
public class LoginAttemptLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Email
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String email;

    @NotBlank
    @Size(max = 45) // IPv6 최대 길이
    @Column(nullable = false, length = 45)
    private String ip;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String device;

    @Column(nullable = false)
    private boolean success = false;

    @Min(0)
    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(nullable = false)
    private boolean aiBlocked = false;

    @Column(name = "features", columnDefinition = "JSON")
    private String features; // JSON 형태로 특징 데이터 저장

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}