package com.agit.peerflow.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-16
 * @description 공지사항(Announcement) 정보를 저장하는 엔티티.
 *              제목(title), 내용(content), 작성자(author), 생성일시(createdAt),
 *              수정일시(updatedAt), 상단 고정 여부(isPinned) 등을 관리한다.
 */
@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "announcement")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Lob
    @Column(nullable = false)
    private String content;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned = false;

    public Announcement(String title, String content, User author, boolean isPinned) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.isPinned = isPinned;
    }

    public void update(String title, String content, boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
    }
}