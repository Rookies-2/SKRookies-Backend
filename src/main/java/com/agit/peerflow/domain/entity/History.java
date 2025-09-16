package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.HistoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author  김현근
 * @version 1.1
 * @since   2025-09-08
 * @description 사용자 알림(히스토리) 정보를 담는 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "history")
@EntityListeners(AuditingEntityListener.class)
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String content;

    @NotBlank
    @Size(max = 500)
    @Column(name = "related_url", nullable = false, length = 500)
    private String relatedUrl;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HistoryType historyType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public History(User user, String content, String relatedUrl, HistoryType historyType) {
        this.user = user;
        this.content = content;
        this.relatedUrl = relatedUrl;
        this.historyType = historyType;
    }

    public static History createHistory(User user, String content, String relatedUrl, HistoryType historyType) {
        return new History(user, content, relatedUrl, historyType);
    }

    public void markAsRead() {
        this.isRead = true;
    }
}