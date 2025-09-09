package com.agit.peerflow.dto.assignment;

import com.agit.peerflow.domain.entity.Submission;
import com.agit.peerflow.domain.enums.AssignmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author  김현근
 * @version 1.2
 * @since   2025-09-08
 * @description 제출물 정보 Response DTO (@Builder 적용)
 */
@Getter
@Builder
public class SubmissionResponse {
    private final Long submissionId;
    private final String studentName;
    private final String fileUrl;
    private final AssignmentStatus status;
    private final LocalDateTime submittedAt;
    private final String grade;
    private final String feedback;

    public static SubmissionResponse from(Submission submission) {
        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .studentName(submission.getStudent().getNickname())
                .fileUrl(submission.getFileUrl())
                .status(submission.getStatus())
                .submittedAt(submission.getSubmittedAt())
                .grade(submission.getGrade())
                .feedback(submission.getFeedback())
                .build();
    }
}