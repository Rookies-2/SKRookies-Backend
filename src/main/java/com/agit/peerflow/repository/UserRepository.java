package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 검색
    Optional<User> findByEmail(String email);

    Optional<User> findByPasswordResetToken(String token);
    // 사용자이름으로 검색
    Optional<User> findByUsername(String userName);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 닉네임 존재 여부 확인
    boolean existsByNickname(String nickname);

    // 사용자이름(ID) 존재 여부 확인
    boolean existsByUsername(String username);

    //Paging 과 Search(검색) 관련 메서드들
    // 사용자 승인상태
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    // 사용자 역할로 검색
    Page<User> findByRole(UserRole role, Pageable pageable);

}
