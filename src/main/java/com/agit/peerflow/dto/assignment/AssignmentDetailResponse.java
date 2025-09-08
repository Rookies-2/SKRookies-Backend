package com.agit.peerflow.dto.assignment;

import com.agit.peerflow.domain.Assignment;
import com.agit.peerflow.domain.Submission;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author  김현근
 * @version 1.2
 * @since   2025-09-08
 * @description 과제 상세 조회를 위한 Response DTO (@Builder 적용)
 */
@Getter
@Builder
public class AssignmentDetailResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String creatorName;
    private final LocalDateTime createdAt;
    private final LocalDateTime dueDate;
    private final List<String> attachmentUrls;
    private final List<SubmissionResponse> submissions;

    public static AssignmentDetailResponse from(Assignment assignment, List<Submission> submissionList) {
        return AssignmentDetailResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .creatorName(assignment.getCreator().getNickname())
                .createdAt(assignment.getCreatedAt())
                .dueDate(assignment.getDueDate())
                .attachmentUrls(assignment.getAttachmentUrls())
                .submissions(submissionList.stream()
                        .map(SubmissionResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}