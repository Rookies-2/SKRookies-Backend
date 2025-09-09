package com.agit.peerflow.dto.assignment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author  김현근
 * @version 1.2
 * @since   2025-09-08
 * @description 과제 채점을 위한 Request DTO (@Builder 적용)
 */
@Getter
@NoArgsConstructor
public class GradeRequestDTO {
    private String grade;
    private String feedback;

    @Builder
    public GradeRequestDTO(String grade, String feedback) {
        this.grade = grade;
        this.feedback = feedback;
    }
}