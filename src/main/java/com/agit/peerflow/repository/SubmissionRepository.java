package com.agit.peerflow.repository;

import com.agit.peerflow.domain.Submission;
import com.agit.peerflow.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author  김현근
 * @version 1.2
 * @since   2025-09-08
 * @description Submission 엔티티 리포지토리 (필요한 메소드 추가)
 */
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * 특정 과제에 속한 모든 제출물을 조회할 때 student(User) 정보도 함께 가져옵니다.
     */
    @Query("SELECT s FROM Submission s JOIN FETCH s.student WHERE s.assignment.id = :assignmentId")
    List<Submission> findAllByAssignmentIdWithStudent(@Param("assignmentId") Long assignmentId);

    /**
     * 특정 학생이 제출한 모든 과제물을 조회합니다. (추가된 메소드)
     */
    List<Submission> findAllByStudent(User student);
}