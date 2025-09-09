package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.Assignment;
import com.agit.peerflow.domain.entity.Submission;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.AssignmentStatus;
import com.agit.peerflow.domain.enums.HistoryType;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.dto.assignment.*;
import com.agit.peerflow.repository.AssignmentRepository;
import com.agit.peerflow.repository.SubmissionRepository;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author  김현근
 * @version 1.5
 * @since   2025-09-08
 * @description 과제 서비스 (파일 저장 기능 임시 비활성화)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final HistoryService historyService;
    // private final FileStorageService fileStorageService; // 👈 1. 파일 서비스 주석 처리

    // createAssignment, gradeSubmission, getAllAssignments, getAssignmentDetails 메소드는 변경 없음

    @Transactional
    public Long createAssignment(AssignmentCreateRequest request, User creator) {
        Assignment newAssignment = Assignment.createAssignment(
                request.getTitle(),
                request.getDescription(),
                creator,
                request.getDueDate(),
                request.getAttachmentUrls()
        );
        Assignment savedAssignment = assignmentRepository.save(newAssignment);

        List<User> students = userRepository.findAllByRole(UserRole.STUDENT);
        String content = String.format("새로운 과제 '%s'가 등록되었습니다.", savedAssignment.getTitle());
        String url = "/assignments/" + savedAssignment.getId();

        students.forEach(student ->
                historyService.createHistory(student, content, url, HistoryType.ASSIGNMENT)
        );
        return savedAssignment.getId();
    }

    /**
     * [학생] 과제 제출 (파일 로직 비활성화)
     */
    @Transactional
    public void submitAssignment(Long assignmentId, SubmissionRequest request, User student) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다. ID: " + assignmentId));

        if (LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new IllegalStateException("제출 기한이 지났습니다.");
        }

        // 👈 2. 파일 저장 로직을 null로 고정
        String fileUrl = null;
        // if (file != null && !file.isEmpty()) {
        //     fileUrl = fileStorageService.store(file);
        // }

        String textContent = request.getTextContent();

        Submission submission = Submission.createSubmission(assignment, student, textContent, fileUrl);
        submissionRepository.save(submission);

        String content = String.format("'%s' 학생이 '%s' 과제를 제출했습니다.", student.getNickname(), assignment.getTitle());
        String url = "/assignments/" + assignmentId;
        historyService.createHistory(assignment.getCreator(), content, url, HistoryType.ASSIGNMENT);
    }

    @Transactional
    public void gradeSubmission(Long submissionId, GradeRequest request, User grader) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("제출물을 찾을 수 없습니다. ID: " + submissionId));

        submission.grade(request.getGrade(), request.getFeedback());

        String content = String.format("'%s' 과제가 채점되었습니다.", submission.getAssignment().getTitle());
        String url = "/assignments/" + submission.getAssignment().getId();
        historyService.createHistory(submission.getStudent(), content, url, HistoryType.ASSIGNMENT);
    }

    public List<AssignmentPreviewResponse> getAllAssignments(User currentUser) {
        List<Assignment> assignments = assignmentRepository.findAll();
        Map<Long, Submission> userSubmissions = submissionRepository.findAllByStudent(currentUser)
                .stream().collect(Collectors.toMap(sub -> sub.getAssignment().getId(), sub -> sub));

        return assignments.stream()
                .map(assignment -> {
                    Submission submission = userSubmissions.get(assignment.getId());
                    AssignmentStatus status = (submission != null) ? submission.getStatus() : AssignmentStatus.NOT_SUBMITTED;
                    return AssignmentPreviewResponse.from(assignment, status);
                })
                .collect(Collectors.toList());
    }

    public AssignmentDetailResponse getAssignmentDetails(Long assignmentId) {
        Assignment assignment = assignmentRepository.findByIdWithCreator(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다. ID: " + assignmentId));

        List<Submission> submissions = submissionRepository.findAllByAssignmentIdWithStudent(assignmentId);

        return AssignmentDetailResponse.from(assignment, submissions);
    }
}