package com.agit.peerflow.service.impl;

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
import com.agit.peerflow.service.AssignmentService;
import com.agit.peerflow.service.HistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author  김현근
 * @version 1.7
 * @since   2025-09-09
 * @description 과제 관련 비즈니스 로직의 구현체 클래스 (Fetch Join 적용 최종본)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final HistoryService historyService;
    // private final FileStorageService fileStorageService; // 파일 기능 구현 시 주석 해제

    @Override
    @Transactional
    public Long createAssignment(AssignmentCreateRequestDTO request, User creator) {
        Assignment newAssignment = Assignment.createAssignment(
                request.getTitle(),
                request.getDescription(),
                creator,
                request.getDueDate(),
                request.getAttachmentUrls()
        );
        Assignment savedAssignment = assignmentRepository.save(newAssignment);

        List<User> students = userRepository.findByRole(UserRole.STUDENT, Pageable.unpaged()).getContent();
        String content = String.format("새로운 과제 '%s'가 등록되었습니다.", savedAssignment.getTitle());
        String url = "/assignments/" + savedAssignment.getId();

        students.forEach(student ->
                historyService.createHistory(student, content, url, HistoryType.ASSIGNMENT)
        );
        return savedAssignment.getId();
    }

    @Override
    @Transactional
    public void submitAssignment(Long assignmentId, SubmissionRequestDTO request, User student) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다. ID: " + assignmentId));

        if (LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new IllegalStateException("제출 기한이 지났습니다.");
        }

        String fileUrl = null;
        String textContent = request.getTextContent();
        Submission submission = Submission.createSubmission(assignment, student, textContent, fileUrl);
        submissionRepository.save(submission);

        String content = String.format("'%s' 학생이 '%s' 과제를 제출했습니다.", student.getNickname(), assignment.getTitle());
        String url = "/assignments/" + assignmentId;
        historyService.createHistory(assignment.getCreator(), content, url, HistoryType.ASSIGNMENT);
    }

    @Override
    @Transactional
    public void updateAssignment(Long assignmentId, AssignmentUpdateRequestDTO request, User updater) {
        Assignment assignment = assignmentRepository.findByIdWithCreator(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다. ID: " + assignmentId));

        boolean isAdmin = updater.getRole().equals(UserRole.ADMIN);
        boolean isCreator = assignment.getCreator().getId().equals(updater.getId());

        if (!isAdmin && !isCreator) {
            throw new SecurityException("과제를 수정할 권한이 없습니다.");
        }

        assignment.update(
                request.getTitle(),
                request.getDescription(),
                request.getDueDate(),
                request.getAttachmentUrls()
        );

        List<User> students = userRepository.findByRole(UserRole.STUDENT, Pageable.unpaged()).getContent();
        String content = String.format("과제 '%s'의 내용이 수정되었습니다.", assignment.getTitle());
        String url = "/assignments/" + assignment.getId();

        students.forEach(student ->
                historyService.createHistory(student, content, url, HistoryType.ASSIGNMENT)
        );
    }

    @Override
    @Transactional
    public void gradeSubmission(Long submissionId, GradeRequestDTO request, User grader) {
        Submission submission = submissionRepository.findByIdWithStudentAndAssignment(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("제출물을 찾을 수 없습니다. ID: " + submissionId));

        submission.grade(request.getGrade(), request.getFeedback());

        String content = String.format("'%s' 과제가 채점되었습니다.", submission.getAssignment().getTitle());
        String url = "/assignments/" + submission.getAssignment().getId();
        historyService.createHistory(submission.getStudent(), content, url, HistoryType.ASSIGNMENT);
    }

    @Override
    public List<AssignmentPreviewResponseDTO> getAllAssignments(User currentUser) {
        List<Assignment> assignments = assignmentRepository.findAll();
        Map<Long, Submission> userSubmissions = submissionRepository.findAllByStudent(currentUser)
                .stream().collect(Collectors.toMap(sub -> sub.getAssignment().getId(), sub -> sub));

        return assignments.stream()
                .map(assignment -> {
                    Submission submission = userSubmissions.get(assignment.getId());
                    AssignmentStatus status = (submission != null) ? submission.getStatus() : AssignmentStatus.NOT_SUBMITTED;
                    return AssignmentPreviewResponseDTO.from(assignment, status);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAssignment(Long assignmentId, User currentUser) {
        // 1. 삭제할 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("과제를 찾을 수 없습니다. ID: " + assignmentId));

        // 2. ‼️ 권한 확인: 현재 사용자가 과제 생성자이거나, ADMIN인지 확인
        boolean isCreator = assignment.getCreator().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isCreator && !isAdmin) {
            throw new AccessDeniedException("이 과제를 삭제할 권한이 없습니다.");
        }

        // 3. 과제 삭제
        assignmentRepository.delete(assignment);
    }

//    @Override
//    public AssignmentDetailResponseDTO getAssignmentDetails(Long assignmentId) {
//        Assignment assignment = assignmentRepository.findByIdWithCreator(assignmentId)
//                .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다. ID: " + assignmentId));
//
//        List<Submission> submissions = submissionRepository.findAllByAssignmentIdWithStudent(assignmentId);
//
//        return AssignmentDetailResponseDTO.from(assignment, submissions);
//    }
}