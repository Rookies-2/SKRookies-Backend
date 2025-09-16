package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.assignment.*;

import java.util.List;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-09
 * @description 과제 관련 비즈니스 로직을 정의하는 서비스 인터페이스
 */
public interface AssignmentService {

    //과제생성
    Long createAssignment(AssignmentCreateRequestDTO request, User creator);

    //과제제출
    void submitAssignment(Long assignmentId, SubmissionRequestDTO request, User student);

    //과제 수정
    void updateAssignment(Long assignmentId, AssignmentUpdateRequestDTO request, User updater);

    //과제 채점
    void gradeSubmission(Long submissionId, GradeRequestDTO request, User grader);

    //전체 과제 목록 조회
    List<AssignmentPreviewResponseDTO> getAllAssignments(User currentUser);

    //과제삭제
    void deleteAssignment(Long assignmentId, User currentUser);

     //과제 상세 정보 조회
    AssignmentDetailResponseDTO getAssignmentDetails(Long assignmentId);
}