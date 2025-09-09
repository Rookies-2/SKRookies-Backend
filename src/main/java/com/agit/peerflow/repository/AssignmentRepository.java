package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Query("SELECT a FROM Assignment a JOIN FETCH a.creator WHERE a.id = :id")
    Optional<Assignment> findByIdWithCreator(@Param("id") Long id);
}