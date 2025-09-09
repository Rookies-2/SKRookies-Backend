package com.agit.peerflow.dto.assignment;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author  김현근
 * @version 1.1
 * @since   2025-09-08
 * @description 과제 제출 시 텍스트 내용을 전달하는 DTO
 */
@Getter
@NoArgsConstructor
public class SubmissionRequest {
    private String textContent; // 학생이 제출하는 텍스트
}