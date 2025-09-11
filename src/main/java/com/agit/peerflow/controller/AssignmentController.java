package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.assignment.*;
import com.agit.peerflow.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Assignment API", description = "과제 생성, 조회, 제출, 채점 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Operation(summary = "새 과제 생성 (강사/관리자)", description = "새로운 과제를 등록합니다. 강사 또는 관리자 권한이 필요합니다.")
    @PostMapping
    public ResponseEntity<Void> createAssignment(
            @RequestBody AssignmentCreateRequestDTO request,
            @AuthenticationPrincipal User creator) {
        Long assignmentId = assignmentService.createAssignment(request, creator);
        return ResponseEntity.created(URI.create("/api/assignments/" + assignmentId)).build();
    }

    @Operation(summary = "전체 과제 목록 조회", description = "모든 과제 목록을 조회합니다. 로그인된 사용자의 과제 제출 상태가 함께 표시됩니다.")
    @GetMapping
    public ResponseEntity<List<AssignmentPreviewResponseDTO>> getAllAssignments(
            @AuthenticationPrincipal User currentUser) {
        List<AssignmentPreviewResponseDTO> assignments = assignmentService.getAllAssignments(currentUser);
        return ResponseEntity.ok(assignments);
    }

//    @Operation(summary = "특정 과제 상세 조회", description = "ID에 해당하는 과제의 상세 정보와 제출물 목록을 조회합니다.")
//    @GetMapping("/{assignmentId}")
//    public ResponseEntity<AssignmentDetailResponseDTO> getAssignmentDetails(
//            @PathVariable Long assignmentId) {
//        AssignmentDetailResponseDTO assignmentDetails = assignmentService.getAssignmentDetails(assignmentId);
//        return ResponseEntity.ok(assignmentDetails);
//    }

    @Operation(summary = "과제 제출 (학생)", description = "ID에 해당하는 과제에 텍스트 또는 파일을 제출합니다. 학생 권한이 필요합니다.")
    @PostMapping("/{assignmentId}/submissions")
    public ResponseEntity<Void> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestBody SubmissionRequestDTO request,
            @AuthenticationPrincipal User student) {
        assignmentService.submitAssignment(assignmentId, request, student);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "과제 정보 수정 (강사/관리자)", description = "과제 생성자 또는 관리자가 과제 내용을 수정합니다.")
    @PatchMapping("/{assignmentId}")
    public ResponseEntity<Void> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody AssignmentUpdateRequestDTO request,
            @AuthenticationPrincipal User updater) {
        assignmentService.updateAssignment(assignmentId, request, updater);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "제출 과제 채점 (강사/관리자)", description = "학생이 제출한 과제를 채점합니다. 강사 또는 관리자 권한이 필요합니다.")
    @PatchMapping("/submissions/{submissionId}")
    public ResponseEntity<Void> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeRequestDTO request,
            @AuthenticationPrincipal User grader) {
        assignmentService.gradeSubmission(submissionId, request, grader);
        return ResponseEntity.ok().build();
    }
}