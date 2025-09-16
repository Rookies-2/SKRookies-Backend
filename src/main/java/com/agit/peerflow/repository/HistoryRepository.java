package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.History;
import com.agit.peerflow.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-16
 * @description 사용자 활동 이력(History) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository.
 *              특정 사용자의 활동 이력을 생성일시 기준 내림차순으로 조회하며,
 *              조회 시 연관된 사용자(User) 엔티티를 함께 로딩한다.
 */
public interface HistoryRepository extends JpaRepository<History, Long> {
    @EntityGraph(attributePaths = "user")
    List<History> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByUser(User user);
}