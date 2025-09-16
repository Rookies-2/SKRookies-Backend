package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.Submission;
import com.agit.peerflow.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author  김현근
 * @version 1.4
 * @since   2025-09-08
 * @description 과제 제출(Submission) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository.
 *              과제 ID, 학생 정보 등을 기준으로 제출 내역을 조회하며,
 *              학생 및 과제 엔티티를 함께 로딩하는 기능을 제공한다.
 */
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Query("""
                SELECT s 
                  FROM Submission s 
            JOIN FETCH s.student 
                 WHERE s.assignment.id = :assignmentId
    """)
    List<Submission> findAllByAssignmentIdWithStudent(@Param("assignmentId") Long assignmentId);

    @EntityGraph(attributePaths = {"assignment"})
    List<Submission> findAllByStudent(User student);

    @Query("""
                SELECT s 
                  FROM Submission s 
            JOIN FETCH s.student 
            JOIN FETCH s.assignment 
                 WHERE s.id = :id
    """)
    Optional<Submission> findByIdWithStudentAndAssignment(@Param("id") Long id);

}