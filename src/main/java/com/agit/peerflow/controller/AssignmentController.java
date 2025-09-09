package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.assignment.*;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; // UserDetails import 추가
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * @author    김현근
 * @version   1.0.0
 * @since     2025-09-8
 * @description
 * - 과제 관련 REST API 컨트롤러
 * - 과제 생성, 조회, 수정 및 제출, 채점 기능 제공
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserRepository userRepository;

    /**
     * [강사/관리자] 새로운 과제를 생성합니다.
     * @param request 과제 생성에 필요한 정보 (제목, 설명, 마감일 등)
     * @param principal 현재 인증된 사용자의 정보
     * @return 생성된 과제의 URI와 함께 201 Created 응답
     */
    @PostMapping
    public ResponseEntity<Void> createAssignment(
            @RequestBody AssignmentCreateRequestDTO request,
            @AuthenticationPrincipal UserDetails principal) {

        // UserDetails에서 이메일(principal.getUsername())을 추출하여 DB에서 User 엔티티를 조회합니다.
        User creator = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long assignmentId = assignmentService.createAssignment(request, creator);
        return ResponseEntity.created(URI.create("/api/assignments/" + assignmentId)).build();
    }

    /**
     * [모든 사용자] 모든 과제 목록을 조회합니다.
     * @param currentUser 현재 로그인한 사용자 정보
     * @return 과제 미리보기 정보 리스트와 200 OK 응답
     */
    @GetMapping
    public ResponseEntity<List<AssignmentPreviewResponseDTO>> getAllAssignments(
            @AuthenticationPrincipal User currentUser) {
        List<AssignmentPreviewResponseDTO> assignments = assignmentService.getAllAssignments(currentUser);
        return ResponseEntity.ok(assignments);
    }

    /**
     * [모든 사용자] 특정 과제의 상세 정보를 조회합니다.
     * @param assignmentId 조회할 과제의 ID
     * @return 과제 상세 정보와 200 OK 응답
     */
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentDetailResponseDTO> getAssignmentDetails(
            @PathVariable Long assignmentId) {
        AssignmentDetailResponseDTO assignmentDetails = assignmentService.getAssignmentDetails(assignmentId);
        return ResponseEntity.ok(assignmentDetails);
    }

    /**
     * [학생] 과제를 제출합니다.
     * @param assignmentId 제출할 과제의 ID
     * @param request 제출 내용 (텍스트, 파일 등)
     * @param student 현재 로그인한 학생 사용자 정보
     * @return 200 OK 응답
     */
    @PostMapping("/{assignmentId}/submissions")
    public ResponseEntity<Void> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestBody SubmissionRequestDTO request,
            @AuthenticationPrincipal User student) {
        assignmentService.submitAssignment(assignmentId, request, student);
        return ResponseEntity.ok().build();
    }

    /**
     * [강사/관리자] 과제 내용을 수정합니다.
     * @param assignmentId 수정할 과제의 ID
     * @param request 수정할 과제 정보
     * @param updater 현재 로그인한 강사/관리자 사용자 정보
     * @return 200 OK 응답
     */
    @PatchMapping("/{assignmentId}")
    public ResponseEntity<Void> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody AssignmentUpdateRequestDTO request,
            @AuthenticationPrincipal User updater) {
        assignmentService.updateAssignment(assignmentId, request, updater);
        return ResponseEntity.ok().build();
    }

    /**
     * [강사/관리자] 과제 제출물을 채점합니다.
     * @param submissionId 채점할 제출물의 ID
     * @param request 채점 정보 (점수, 피드백 등)
     * @param grader 현재 로그인한 강사/관리자 사용자 정보
     * @return 200 OK 응답
     */
    @PatchMapping("/submissions/{submissionId}")
    public ResponseEntity<Void> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeRequestDTO request,
            @AuthenticationPrincipal User grader) {
        assignmentService.gradeSubmission(submissionId, request, grader);
        return ResponseEntity.ok().build();
    }
}