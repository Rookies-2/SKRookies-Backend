package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
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
 * @version 1.5
 * @since   2025-09-08
 * @description 과제 컨트롤러 (파일 저장 기능 임시 비활성화)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    // ... createAssignment, getAllAssignments, getAssignmentDetails, gradeSubmission 메소드는 변경 없음 ...

    @PostMapping
    public ResponseEntity<Void> createAssignment(
            @RequestBody AssignmentCreateRequest request,
            @AuthenticationPrincipal User creator) {
        Long assignmentId = assignmentService.createAssignment(request, creator);
        return ResponseEntity.created(URI.create("/api/assignments/" + assignmentId)).build();
    }

    @GetMapping
    public ResponseEntity<List<AssignmentPreviewResponse>> getAllAssignments(
            @AuthenticationPrincipal User currentUser) {
        List<AssignmentPreviewResponse> assignments = assignmentService.getAllAssignments(currentUser);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentDetailResponse> getAssignmentDetails(
            @PathVariable Long assignmentId) {
        AssignmentDetailResponse assignmentDetails = assignmentService.getAssignmentDetails(assignmentId);
        return ResponseEntity.ok(assignmentDetails);
    }

    /**
     * 과제 제출 API (학생) - 파일 로직 비활성화
     */
    @PostMapping("/{assignmentId}/submissions")
    public ResponseEntity<Void> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestBody SubmissionRequest request, // 👈 @RequestPart 대신 @RequestBody 사용
            // @RequestPart(value = "file", required = false) MultipartFile file, // 👈 파일 파라미터 주석 처리
            @AuthenticationPrincipal User student) {
        assignmentService.submitAssignment(assignmentId, request, student); // 👈 file 파라미터 제거
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/submissions/{submissionId}")
    public ResponseEntity<Void> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeRequest request,
            @AuthenticationPrincipal User grader) {
        assignmentService.gradeSubmission(submissionId, request, grader);
        return ResponseEntity.ok().build();
    }
}