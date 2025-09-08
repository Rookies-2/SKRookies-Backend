package com.agit.peerflow.repository;

import com.agit.peerflow.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author  신명철 (임시 작성: 김현근)
 * @version 1.0
 * @since   2025-09-08
 * @description User 엔티티에 대한 데이터 접근을 위한 리포지토리
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // username으로 사용자를 찾는 메소드 (로그인 및 DataLoader에서 사용)
    Optional<User> findByUsername(String username);
}