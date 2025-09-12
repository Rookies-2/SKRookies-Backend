package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.LoginAttemptLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface LoginAttemptLogRepository extends JpaRepository<LoginAttemptLog, Long> {

    @Query("SELECT COUNT(l) FROM LoginAttemptLog l WHERE l.email = :email AND l.createdAt BETWEEN :start AND :end")
    int countTodayByUserEmail(@Param("email") String email,
                           @Param("start") LocalDateTime start,
                           @Param("end") LocalDateTime end);
}