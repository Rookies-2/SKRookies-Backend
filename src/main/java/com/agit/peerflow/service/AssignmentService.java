package com.agit.peerflow.service;

import com.agit.peerflow.domain.Assignment;
import com.agit.peerflow.domain.Submission;
import com.agit.peerflow.domain.User;
import com.agit.peerflow.domain.enums.AssignmentStatus;
import com.agit.peerflow.domain.enums.NotificationType;
import com.agit.peerflow.domain.enums.Role;
import com.agit.peerflow.dto.assignment.*;
import com.agit.peerflow.repository.AssignmentRepository;
import com.agit.peerflow.repository.SubmissionRepository;
import com.agit.peerflow.repository.UserRepository; // UserRepository import 추가
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
 * @version 1.3
 * @since   2025-09-08
 * @description 과제 관련 비즈니스 로직 (과제 생성 시 알림 기능 추가)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository; // UserRepository 의존성 주입
    private final NotificationService notificationService; // NotificationService 의존성 주입
    // private final FileStorageService fileStorageService; // 로컬 파일 저장 서비스

    /**
     * [강사/관리자] 과제 생성
     */
    @Transactional
    public Long createAssignment(AssignmentCreateRequest request, User creator) {
        // TODO: creator의 Role이 TEACHER 또는 ADMIN인지 확인하는 로직 추가

        Assignment newAssignment = Assignment.createAssignment(
                request.getTitle(),
                request.getDescription(),
                creator,
                request.getDueDate(),
                request.getAttachmentUrls()
        );

        Assignment savedAssignment = assignmentRepository.save(newAssignment);

        // 1. 모든 학생 유저를 조회합니다.
        List<User> students = userRepository.findAllByRole(Role.STUDENT);

        // 2. 알림에 필요한 내용을 구성합니다.
        String content = String.format("새로운 과제 '%s'가 등록되었습니다.", savedAssignment.getTitle());
        String url = "/assignments/" + savedAssignment.getId();

        // 3. 각 학생에게 알림을 생성합니다.
        students.forEach(student ->
                notificationService.createNotification(student, content, url, NotificationType.ASSIGNMENT)
        );
        // ==========================================================

        return savedAssignment.getId();
    }

    /**
     * [학생] 과제 제출
     */
    @Transactional
    public void submitAssignment(Long assignmentId, MultipartFile file, User student) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다. ID: " + assignmentId));

        if (LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new IllegalStateException("제출 기한이 지났습니다.");
        }

        // TODO: FileStorageService를 사용하여 파일 저장 로직 구현
        String fileUrl = "temp-file-url-for-" + file.getOriginalFilename();

        Submission submission = Submission.createSubmission(assignment, student, fileUrl);
        submissionRepository.save(submission);

        // TODO: 과제 제출 시 강사(assignment.getCreator())에게 알림 보내기
        String content = String.format("'%s' 학생이 '%s' 과제를 제출했습니다.", student.getNickname(), assignment.getTitle());
        String url = "/assignments/" + assignmentId;
        notificationService.createNotification(assignment.getCreator(), content, url, NotificationType.ASSIGNMENT);
    }

    /**
     * [강사/관리자] 과제 채점
     */
    @Transactional
    public void gradeSubmission(Long submissionId, GradeRequest request, User grader) {
        // TODO: grader의 Role이 TEACHER 또는 ADMIN인지 확인하는 로직 추가

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("제출물을 찾을 수 없습니다. ID: " + submissionId));

        submission.grade(request.getGrade(), request.getFeedback());

        // 채점 완료 시 학생(submission.getStudent())에게 알림 보내기
        String content = String.format("'%s' 과제가 채점되었습니다.", submission.getAssignment().getTitle());
        String url = "/assignments/" + submission.getAssignment().getId();
        notificationService.createNotification(submission.getStudent(), content, url, NotificationType.ASSIGNMENT);
    }

    // ... (getAllAssignments, getAssignmentDetails 메소드는 변경 없음)
    /**
     * [공통] 과제 목록 조회
     */
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

    /**
     * [공통] 과제 상세 정보 조회
     */
    public AssignmentDetailResponse getAssignmentDetails(Long assignmentId) {
        Assignment assignment = assignmentRepository.findByIdWithCreator(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다. ID: " + assignmentId));

        List<Submission> submissions = submissionRepository.findAllByAssignmentIdWithStudent(assignmentId);

        return AssignmentDetailResponse.from(assignment, submissions);
    }
}