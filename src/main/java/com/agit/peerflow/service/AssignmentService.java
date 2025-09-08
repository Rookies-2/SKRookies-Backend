package com.agit.peerflow.service;

import com.agit.peerflow.domain.Assignment;
import com.agit.peerflow.domain.Submission;
import com.agit.peerflow.domain.User;
import com.agit.peerflow.domain.enums.AssignmentStatus;
import com.agit.peerflow.dto.assignment.*;
import com.agit.peerflow.repository.AssignmentRepository;
import com.agit.peerflow.repository.SubmissionRepository;
// TODO: UserRepository, S3Uploader, NotificationService import 필요
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
 * @version 1.2
 * @since   2025-09-08
 * @description 과제 관련 비즈니스 로직을 처리하는 서비스 클래스 (Fetch Join 적용)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    // TODO: 아래 의존성들을 실제 구현체로 주입받아야 합니다.
    // private final UserRepository userRepository;
    // private final NotificationService notificationService;
    // private final S3Uploader s3Uploader;

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

        return assignmentRepository.save(newAssignment).getId();
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

        // TODO: S3 파일 업로드 로직
        // String fileUrl = s3Uploader.upload(file, "submissions");
        String fileUrl = "temp-file-url-for-" + file.getOriginalFilename(); // 임시 URL

        Submission submission = Submission.createSubmission(assignment, student, fileUrl);
        submissionRepository.save(submission);

        // TODO: 과제 제출 시 강사(assignment.getCreator())에게 알림 보내기
        // notificationService.createNotification(assignment.getCreator(), ...);
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

        // TODO: 채점 완료 시 학생(submission.getStudent())에게 알림 보내기
        // notificationService.createNotification(submission.getStudent(), ...);
    }

    /**
     * [공통] 과제 목록 조회
     */
    public List<AssignmentPreviewResponse> getAllAssignments(User currentUser) {
        List<Assignment> assignments = assignmentRepository.findAll();
        // TODO: 성능 최적화를 위해 assignments에 대한 submission을 한 번에 조회하는 로직 추가
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