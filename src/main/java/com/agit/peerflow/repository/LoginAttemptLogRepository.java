package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.LoginAttemptLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * @author  신명철
 * @version 1.0
 * @since   2025-09-16
 * @description 로그인 시도 이력(LoginAttemptLog) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository.
 *              특정 이메일의 로그인 시도 횟수를 기간별로 집계하는 기능을 제공한다.
 */
public interface LoginAttemptLogRepository extends JpaRepository<LoginAttemptLog, Long> {

    @Query("""
        SELECT COUNT(l)
        FROM LoginAttemptLog l
        WHERE l.email = :email
          AND l.createdAt BETWEEN :start AND :end
    """)
    long countTodayByUserEmail(@Param("email") String email,
                               @Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);
}
