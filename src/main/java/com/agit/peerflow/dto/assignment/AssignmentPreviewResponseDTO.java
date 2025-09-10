package com.agit.peerflow.dto.assignment;

import com.agit.peerflow.domain.entity.Assignment;
import com.agit.peerflow.domain.enums.AssignmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author  김현근
 * @version 1.2
 * @since   2025-09-08
 * @description 과제 목록 조회를 위한 Response DTO (@Builder 적용)
 */
@Getter
@Builder
public class AssignmentPreviewResponseDTO {
    private final Long id;
    private final String title;
    private final String creatorName;
    private final LocalDateTime createdAt;
    private final LocalDateTime dueDate;
    private final AssignmentStatus mySubmissionStatus;

    public static AssignmentPreviewResponseDTO from(Assignment assignment, AssignmentStatus myStatus) {
        return AssignmentPreviewResponseDTO.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .creatorName(assignment.getCreator().getNickname())
                .createdAt(assignment.getCreatedAt())
                .dueDate(assignment.getDueDate())
                .mySubmissionStatus(myStatus)
                .build();
    }
}