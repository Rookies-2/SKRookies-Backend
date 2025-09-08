package com.agit.peerflow.domain;

import com.agit.peerflow.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-08
 * @description 사용자에게 전달될 알림(히스토리) 정보를 담는 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 알림을 수신할 사용자

    @Column(nullable = false)
    private String content; // 알림 내용

    @Column(nullable = false)
    private String relatedUrl; // 클릭 시 이동할 URL

    @Column(nullable = false)
    private boolean isRead = false; // 읽음 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //== 생성 로직 ==//
    private Notification(User user, String content, String relatedUrl, NotificationType notificationType) {
        this.user = user;
        this.content = content;
        this.relatedUrl = relatedUrl;
        this.notificationType = notificationType;
    }

    /**
     * 정적 팩토리 메소드
     */
    public static Notification createNotification(User user, String content, String relatedUrl, NotificationType notificationType) {
        return new Notification(user, content, relatedUrl, notificationType);
    }

    //== 비즈니스 로직 ==//
    public void markAsRead() {
        this.isRead = true;
    }
}