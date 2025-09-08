package com.agit.peerflow.controller;

import com.agit.peerflow.domain.User;
import com.agit.peerflow.dto.assignment.*;
import com.agit.peerflow.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.util.List;

/**
 * @author  김현근
 * @version 1.2
 * @since   2025-09-08
 * @description 과제 관련 API 요청을 처리하는 컨트롤러 (@AuthenticationPrincipal 적용)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    /**
     * 과제 생성 API (강사/관리자)
     */
    // @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Void> createAssignment(
            @RequestBody AssignmentCreateRequest request,
            @AuthenticationPrincipal User creator) { // 현재 로그인한 사용자 정보
        Long assignmentId = assignmentService.createAssignment(request, creator);
        return ResponseEntity.created(URI.create("/api/assignments/" + assignmentId)).build();
    }

    /**
     * 과제 목록 조회 API (공통)
     */
    @GetMapping
    public ResponseEntity<List<AssignmentPreviewResponse>> getAllAssignments(
            @AuthenticationPrincipal User currentUser) {
        List<AssignmentPreviewResponse> assignments = assignmentService.getAllAssignments(currentUser);
        return ResponseEntity.ok(assignments);
    }

    /**
     * 과제 상세 조회 API (공통)
     */
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentDetailResponse> getAssignmentDetails(
            @PathVariable Long assignmentId) {
        AssignmentDetailResponse assignmentDetails = assignmentService.getAssignmentDetails(assignmentId);
        return ResponseEntity.ok(assignmentDetails);
    }

    /**
     * 과제 제출 API (학생)
     */
    // @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{assignmentId}/submissions")
    public ResponseEntity<Void> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal User student) {
        assignmentService.submitAssignment(assignmentId, file, student);
        return ResponseEntity.ok().build();
    }

    /**
     * 과제 채점 API (강사/관리자)
     */
    // @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PatchMapping("/submissions/{submissionId}")
    public ResponseEntity<Void> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeRequest request,
            @AuthenticationPrincipal User grader) {
        assignmentService.gradeSubmission(submissionId, request, grader);
        return ResponseEntity.ok().build();
    }
}