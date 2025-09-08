// repository/SubmissionRepository.java

package com.agit.peerflow.repository;

import com.agit.peerflow.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author  김현근
 * @version 1.1
 * @since   2025-09-08
 * @description Submission 엔티티 리포지토리 (Fetch Join 추가)
 */
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * 특정 과제에 속한 모든 제출물을 조회할 때 student(User) 정보도 함께 가져옵니다.
     */
    @Query("SELECT s FROM Submission s JOIN FETCH s.student WHERE s.assignment.id = :assignmentId")
    List<Submission> findAllByAssignmentIdWithStudent(@Param("assignmentId") Long assignmentId);
}