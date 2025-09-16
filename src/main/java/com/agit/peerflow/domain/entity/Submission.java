package com.agit.peerflow.domain.entity;

import com.agit.peerflow.domain.enums.AssignmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author  김현근
 * @version 1.4
 * @since   2025-09-08
 * @description 학생 제출물 엔티티 (텍스트 제출 필드 추가 및 fileUrl 선택사항으로 변경)
 */
@Entity
@Getter
@Table(name = "submission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Lob
    private String textContent;

    @Column(length = 500)
    private String fileUrl;

    @Size(max = 20)
    private String grade;

    @Lob
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    private Submission(Assignment assignment, User student, String textContent, String fileUrl) {
        if ((textContent == null || textContent.isBlank()) && fileUrl == null) {
            throw new IllegalArgumentException("텍스트나 파일 중 하나는 반드시 제출해야 합니다.");
        }
        this.assignment = assignment;
        this.student = student;
        this.textContent = textContent;
        this.fileUrl = fileUrl;
        this.status = AssignmentStatus.SUBMITTED;
    }

    public static Submission createSubmission(Assignment assignment, User student, String textContent, String fileUrl) {
        return new Submission(assignment, student, textContent, fileUrl);
    }

    public void grade(String grade, String feedback) {
        if (grade == null || grade.isBlank()) {
            throw new IllegalArgumentException("성적은 필수입니다.");
        }

        this.grade = grade;
        this.feedback = feedback;
        this.status = AssignmentStatus.GRADED;
    }
}