package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.PasswordResetLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * @author  신명철
 * @version 1.0
 * @since   2025-09-16
 * @description 비밀번호 재설정 로그(PasswordResetLog) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository.
 *              특정 이메일의 비밀번호 재설정 시도 횟수를 기간별로 조회하는 기능을 제공한다.
 */
public interface PasswordResetLogRepository extends JpaRepository<PasswordResetLog, Long> {

    // 오늘 비밀번호 재설정 시도 횟수 조회
    @Query("""
        SELECT COUNT(p)
        FROM PasswordResetLog p
        WHERE p.email = :email
          AND p.createdAt BETWEEN :start AND :end
    """)
    int countTodayByEmail(@Param("email") String email,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);
}
