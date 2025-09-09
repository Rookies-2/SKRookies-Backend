package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.assignment.*;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Void> createAssignment(
            @RequestBody AssignmentCreateRequestDTO request,
            @AuthenticationPrincipal UserDetails principal) {

        // principal.getUsername() == email
        User creator = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long assignmentId = assignmentService.createAssignment(request, creator);
        return ResponseEntity.created(URI.create("/api/assignments/" + assignmentId)).build();
    }

    @GetMapping
    public ResponseEntity<List<AssignmentPreviewResponseDTO>> getAllAssignments(
            @AuthenticationPrincipal User currentUser) {
        List<AssignmentPreviewResponseDTO> assignments = assignmentService.getAllAssignments(currentUser);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentDetailResponseDTO> getAssignmentDetails(
            @PathVariable Long assignmentId) {
        AssignmentDetailResponseDTO assignmentDetails = assignmentService.getAssignmentDetails(assignmentId);
        return ResponseEntity.ok(assignmentDetails);
    }

    @PostMapping("/{assignmentId}/submissions")
    public ResponseEntity<Void> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestBody SubmissionRequestDTO request,
            @AuthenticationPrincipal User student) {
        assignmentService.submitAssignment(assignmentId, request, student);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{assignmentId}")
    public ResponseEntity<Void> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody AssignmentUpdateRequestDTO request,
            @AuthenticationPrincipal User updater) {
        assignmentService.updateAssignment(assignmentId, request, updater);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/submissions/{submissionId}")
    public ResponseEntity<Void> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeRequestDTO request,
            @AuthenticationPrincipal User grader) {
        assignmentService.gradeSubmission(submissionId, request, grader);
        return ResponseEntity.ok().build();
    }
}