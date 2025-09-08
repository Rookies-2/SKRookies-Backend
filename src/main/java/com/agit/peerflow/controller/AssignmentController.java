//package com.agit.peerflow.controller;
//
//import com.agit.peerflow.domain.User;
//import com.agit.peerflow.dto.assignment.*;
//import com.agit.peerflow.service.AssignmentService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.net.URI;
//import java.util.List;
//
///**
// * @author  김현근
// * @version 1.2
// * @since   2025-09-08
// * @description 과제 관련 API 요청을 처리하는 컨트롤러 (@AuthenticationPrincipal 적용)
// */
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/assignments")
//public class AssignmentController {
//
//    private final AssignmentService assignmentService;
//
//    /**
//     * 과제 생성 API (강사/관리자)
//     */
//    // @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
//    @PostMapping
//    public ResponseEntity<Void> createAssignment(
//            @RequestBody AssignmentCreateRequest request,
//            @AuthenticationPrincipal User creator) { // 현재 로그인한 사용자 정보
//        Long assignmentId = assignmentService.createAssignment(request, creator);
//        return ResponseEntity.created(URI.create("/api/assignments/" + assignmentId)).build();
//    }
//
//    /**
//     * 과제 목록 조회 API (공통)
//     */
//    @GetMapping
//    public ResponseEntity<List<AssignmentPreviewResponse>> getAllAssignments(
//            @AuthenticationPrincipal User currentUser) {
//        List<AssignmentPreviewResponse> assignments = assignmentService.getAllAssignments(currentUser);
//        return ResponseEntity.ok(assignments);
//    }
//
//    /**
//     * 과제 상세 조회 API (공통)
//     */
//    @GetMapping("/{assignmentId}")
//    public ResponseEntity<AssignmentDetailResponse> getAssignmentDetails(
//            @PathVariable Long assignmentId) {
//        AssignmentDetailResponse assignmentDetails = assignmentService.getAssignmentDetails(assignmentId);
//        return ResponseEntity.ok(assignmentDetails);
//    }
//
//    /**
//     * 과제 제출 API (학생)
//     */
//    // @PreAuthorize("hasRole('STUDENT')")
//    @PostMapping("/{assignmentId}/submissions")
//    public ResponseEntity<Void> submitAssignment(
//            @PathVariable Long assignmentId,
//            @RequestPart("file") MultipartFile file,
//            @AuthenticationPrincipal User student) {
//        assignmentService.submitAssignment(assignmentId, file, student);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * 과제 채점 API (강사/관리자)
//     */
//    // @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
//    @PatchMapping("/submissions/{submissionId}")
//    public ResponseEntity<Void> gradeSubmission(
//            @PathVariable Long submissionId,
//            @RequestBody GradeRequest request,
//            @AuthenticationPrincipal User grader) {
//        assignmentService.gradeSubmission(submissionId, request, grader);
//        return ResponseEntity.ok().build();
//    }
//}

package com.agit.peerflow.controller;

import com.agit.peerflow.domain.User;
import com.agit.peerflow.dto.assignment.*;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

/**
 * @author  김현근
 * @version 1.2
 * @since   2025-09-08
 * @description 과제 관련 API 컨트롤러 (로그인 기능 연동 전 임시 테스트 버전)
 * @note    @AuthenticationPrincipal 대신 @RequestParam으로 userId를 직접 받아 테스트합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserRepository userRepository; // 테스트를 위해 임시로 UserRepository 주입

    /**
     * 과제 생성 API (강사/관리자)
     */
    @PostMapping
    public ResponseEntity<Void> createAssignment(
            @RequestBody AssignmentCreateRequest request,
            @RequestParam Long userId) { // @AuthenticationPrincipal 대신 @RequestParam으로 userId를 직접 받습니다.

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("테스트 유저를 찾을 수 없습니다. ID: " + userId));

        Long assignmentId = assignmentService.createAssignment(request, creator);
        return ResponseEntity.created(URI.create("/api/assignments/" + assignmentId)).build();
    }

    /**
     * 과제 목록 조회 API (공통)
     */
    @GetMapping
    public ResponseEntity<List<AssignmentPreviewResponse>> getAllAssignments(
            @RequestParam Long userId) { // 현재 사용자를 식별하기 위해 userId를 받습니다.

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("테스트 유저를 찾을 수 없습니다. ID: " + userId));

        List<AssignmentPreviewResponse> assignments = assignmentService.getAllAssignments(currentUser);
        return ResponseEntity.ok(assignments);
    }

    /**
     * 과제 상세 조회 API (공통)
     */
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentDetailResponse> getAssignmentDetails(
            @PathVariable Long assignmentId) {
        // 이 API는 특정 사용자의 정보가 필요 없으므로 userId를 받지 않아도 됩니다.
        AssignmentDetailResponse assignmentDetails = assignmentService.getAssignmentDetails(assignmentId);
        return ResponseEntity.ok(assignmentDetails);
    }

    /**
     * 과제 제출 API (학생)
     */
    @PostMapping("/{assignmentId}/submissions")
    public ResponseEntity<Void> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestPart("file") MultipartFile file,
            @RequestParam Long userId) {

        User student = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("테스트 유저를 찾을 수 없습니다. ID: " + userId));

        assignmentService.submitAssignment(assignmentId, file, student);
        return ResponseEntity.ok().build();
    }

    /**
     * 과제 채점 API (강사/관리자)
     */
    @PatchMapping("/submissions/{submissionId}")
    public ResponseEntity<Void> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeRequest request,
            @RequestParam Long userId) {

        User grader = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("테스트 유저를 찾을 수 없습니다. ID: " + userId));

        assignmentService.gradeSubmission(submissionId, request, grader);
        return ResponseEntity.ok().build();
    }
}