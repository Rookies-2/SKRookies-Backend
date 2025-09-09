package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.Submission;
import com.agit.peerflow.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Query("SELECT s FROM Submission s JOIN FETCH s.student WHERE s.assignment.id = :assignmentId")
    List<Submission> findAllByAssignmentIdWithStudent(@Param("assignmentId") Long assignmentId);

    List<Submission> findAllByStudent(User student);
}