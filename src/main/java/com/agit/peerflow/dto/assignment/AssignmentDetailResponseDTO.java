package com.agit.peerflow.dto.assignment;

import com.agit.peerflow.domain.entity.Assignment;
import com.agit.peerflow.domain.entity.Submission;
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
public class AssignmentDetailResponseDTO {
    private final Long id;
    private final String title;
    private final String description;
    private final String creatorName;
    //private final String textContext;
    private final LocalDateTime createdAt;
    private final LocalDateTime dueDate;
    private final List<String> attachmentUrls;
    private final List<SubmissionResponseDTO> submissions;

    // 엔티티 대신, 서비스에서 꺼낸 값들을 받음
    public static AssignmentDetailResponseDTO of(
            Long id,
            String title,
            String description,
            String creatorName,
          //  String textContext,
            LocalDateTime createdAt,
            LocalDateTime dueDate,
            List<String> attachmentUrls,
            List<Submission> submissionList
    ) {
        return AssignmentDetailResponseDTO.builder()
                .id(id)
                .title(title)
                .description(description)
                .creatorName(creatorName)
              //  .textContext(textContext)
                .createdAt(createdAt)
                .dueDate(dueDate)
                .attachmentUrls(attachmentUrls)
                .submissions(submissionList.stream()
                        .map(SubmissionResponseDTO::from)
                        .collect(Collectors.toList()))
                .build();
    }
}