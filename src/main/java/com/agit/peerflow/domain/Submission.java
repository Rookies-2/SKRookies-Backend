package com.agit.peerflow.domain;

import com.agit.peerflow.domain.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author  김현근
 * @version 1.2
 * @since   2025-09-08
 * @description 학생 제출물 정보를 담는 엔티티 클래스 (정적 팩토리 메소드 적용)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private String fileUrl;

    private String grade;

    @Lob
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    //== 생성 로직 ==//
    private Submission(Assignment assignment, User student, String fileUrl) {
        this.assignment = assignment;
        this.student = student;
        this.fileUrl = fileUrl;
        this.status = AssignmentStatus.SUBMITTED;
    }

    /**
     * 정적 팩토리 메소드
     */
    public static Submission createSubmission(Assignment assignment, User student, String fileUrl) {
        return new Submission(assignment, student, fileUrl);
    }

    //== 비즈니스 로직 ==//
    public void grade(String grade, String feedback) {
        this.grade = grade;
        this.feedback = feedback;
        this.status = AssignmentStatus.GRADED;
    }
}