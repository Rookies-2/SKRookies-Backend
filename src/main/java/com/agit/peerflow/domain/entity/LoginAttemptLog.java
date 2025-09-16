package com.agit.peerflow.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "login_attempt_log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LoginAttemptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private String device;

    private boolean success;

    @Column(name = "attempt_count")
    private int attemptCount;

    private boolean aiBlocked;

    @Column(name = "features", columnDefinition = "JSON")
    private String features; // JSON 형태로 특징 데이터 저장

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}