package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 검색
    Optional<User> findByEmail(String email);

    // 사용자이름으로 검색 (UserName -> username)
    Optional<User> findByUsername(String username);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 닉네임 존재 여부 확인
    boolean existsByNickname(String nickname);

    // 사용자이름(ID) 존재 여부 확인
    boolean existsByUsername(String username);

    // 상태별 사용자 페이징 조회
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    // 역할별 사용자 페이징 조회
    Page<User> findByRole(UserRole role, Pageable pageable);

    // findById는 JpaRepository에 이미 있으므로 중복 선언 불필요
}