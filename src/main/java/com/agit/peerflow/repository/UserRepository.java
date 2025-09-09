package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 검색
    Optional<User> findByEmail(String email);
    // 사용자이름으로 검색
    Optional<User> findByUsername(String username);
    // 사용자 승인상태
    List<User> findByStatus(UserStatus status);
    List<User> findAllByRole(UserRole role);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 닉네임 존재 여부 확인
    boolean existsByNickname(String nickname);


}
