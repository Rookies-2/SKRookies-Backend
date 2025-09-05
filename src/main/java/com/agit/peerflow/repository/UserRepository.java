package com.agit.peerflow.repository;

import com.agit.peerflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //이메일로 검색
    Optional<User> findByEmail(String email);
    //사용자 승인상태
    List<User> findByStatus(String status);
    //사용자역할로 검색
    List<User> findByRole(String role);
}
