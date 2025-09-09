package com.agit.peerflow.dto.assignment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-09
 * @description 과제 수정을 위한 Request DTO
 */
@Getter
@NoArgsConstructor
public class AssignmentUpdateRequestDTO {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private List<String> attachmentUrls;

    @Builder
    public AssignmentUpdateRequestDTO(String title, String description, LocalDateTime dueDate, List<String> attachmentUrls) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.attachmentUrls = attachmentUrls;
    }
}