package com.agit.peerflow.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "login_attempt_log")
@Builder
public class LoginAttemptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private String device;

    private boolean success;

    @Column(name = "attempt_count")
    private int attemptCount;

    // Flask AI 모델 예측 결과
    private Integer modelPrediction;

    @Column(name = "ai_blocked")
    private boolean aiBlocked;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}