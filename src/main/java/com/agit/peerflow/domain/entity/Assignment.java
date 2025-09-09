package com.agit.peerflow.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author  김현근
 * @version 1.3
 * @since   2025-09-08
 * @description 과제 정보를 담는 엔티티 클래스
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "assignment_attachments", joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "attachment_url")
    private List<String> attachmentUrls;

    // 생성자에서 createdAt 필드를 제거
    private Assignment(String title, String description, User creator, LocalDateTime dueDate, List<String> attachmentUrls) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.dueDate = dueDate;
        this.attachmentUrls = attachmentUrls;
    }

    // 정적 팩토리 메서드도 createdAt 인자를 제거
    public static Assignment createAssignment(String title, String description, User creator, LocalDateTime dueDate, List<String> attachmentUrls) {
        return new Assignment(title, description, creator, dueDate, attachmentUrls);
    }

    public void update(String title, String description, LocalDateTime dueDate, List<String> attachmentUrls) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.attachmentUrls = attachmentUrls;
    }
}