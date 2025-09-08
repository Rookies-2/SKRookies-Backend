// repository/AssignmentRepository.java

package com.agit.peerflow.repository;

import com.agit.peerflow.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author  김현근
 * @version 1.1
 * @since   2025-09-08
 * @description Assignment 엔티티 리포지토리 (Fetch Join 추가)
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    /**
     * ID로 과제를 조회할 때 creator(User) 정보도 함께 fetch join으로 가져옵니다.
     */
    @Query("SELECT a FROM Assignment a JOIN FETCH a.creator WHERE a.id = :id")
    Optional<Assignment> findByIdWithCreator(@Param("id") Long id);
}