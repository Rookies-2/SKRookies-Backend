package com.agit.peerflow.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author  신명철
 * @version 1.0
 * @since   2025-09-16
 * @description 비밀번호 재설정 요청 이력을 저장하는 엔티티.
 *              요청한 사용자, 이메일, IP, 디바이스 정보, 시도 횟수, AI 차단 여부, 생성일시 등을 기록한다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "password_reset_log")
@EntityListeners(AuditingEntityListener.class)
public class PasswordResetLog {

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

    @Min(0)
    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private boolean aiBlocked;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
}