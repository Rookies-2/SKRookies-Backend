package com.agit.peerflow.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Table(name = "assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank
    @Lob
    @Column(nullable = false)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @FutureOrPresent
    @Column(nullable = false)
    private LocalDateTime dueDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "assignment_attachments", joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "attachment_url", length = 500)
    private List<@URL String> attachmentUrls = new ArrayList<>();

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();

    private Assignment(String title, String description, User creator, LocalDateTime dueDate, List<String> attachmentUrls) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.dueDate = dueDate;
        this.attachmentUrls = attachmentUrls != null ? attachmentUrls : new ArrayList<>();
    }

    public static Assignment createAssignment(String title, String description, User creator, LocalDateTime dueDate, List<String> attachmentUrls) {
        return new Assignment(title, description, creator, dueDate, attachmentUrls);
    }

    public void update(String title, String description, LocalDateTime dueDate, List<String> attachmentUrls) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.attachmentUrls = attachmentUrls != null ? attachmentUrls : new ArrayList<>();
    }
}