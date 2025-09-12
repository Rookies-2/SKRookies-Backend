package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.PasswordResetLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PasswordResetLogRepository extends JpaRepository<PasswordResetLog, Long> {

    // 오늘 비밀번호 재설정 시도 횟수 조회
    @Query("SELECT COUNT(p) FROM PasswordResetLog p " +
            "WHERE p.email = :email AND " +
            "p.createdAt BETWEEN :start AND :end")
    int countTodayByEmail(@Param("email") String email,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);
}
