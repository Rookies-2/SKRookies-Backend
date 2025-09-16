package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.Assignment;
import com.agit.peerflow.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-16
 * @description 과제(Assignment) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository.
 *              과제 생성자(Creator)와 첨부파일(attachmentUrls) 정보를 함께 로딩하여 조회하는 기능을 제공한다.
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Query("""
        SELECT DISTINCT a
        FROM Assignment a
        JOIN FETCH a.creator
        LEFT JOIN FETCH a.attachmentUrls
        WHERE a.id = :id
    """)
    Optional<Assignment> findByIdWithCreator(@Param("id") Long id);

}

