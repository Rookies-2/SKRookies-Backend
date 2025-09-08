package com.agit.peerflow.repository;

import com.agit.peerflow.domain.User;
import com.agit.peerflow.domain.enums.Role; // Role import 추가
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // List import 추가
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // STUDENT 역할을 가진 모든 사용자를 조회하는 메소드 (추가)
    List<User> findAllByRole(Role role);
}