package com.agit.peerflow.domain;

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
 * @version 1.2
 * @since   2025-09-08
 * @description 과제 정보를 담는 엔티티 클래스 (정적 팩토리 메소드 적용)
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

    //== 생성 로직 ==//
    private Assignment(String title, String description, User creator, LocalDateTime dueDate, List<String> attachmentUrls) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.dueDate = dueDate;
        this.attachmentUrls = attachmentUrls;
    }

    /**
     * 정적 팩토리 메소드
     */
    public static Assignment createAssignment(String title, String description, User creator, LocalDateTime dueDate, List<String> attachmentUrls) {
        return new Assignment(title, description, creator, dueDate, attachmentUrls);
    }
}