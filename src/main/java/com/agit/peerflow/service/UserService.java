package com.agit.peerflow.service;

import com.agit.peerflow.entity.User;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * User 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 회원가입 요청 처리
     * - 가입 시 상태를 PENDING으로 설정
     */
    public User registerUser(User user) {
        user.setStatus("PENDING"); // 기본 승인 대기
        return userRepository.save(user);
    }

    /**
     * 승인 대기 사용자 목록 조회
     */
    public List<User> getPendingUsers() {
        return userRepository.findByStatus("PENDING");
    }

    /**
     * 사용자 승인 처리
     */
    public Optional<User> approveUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> u.setStatus("ACTIVE")); // 상태 ACTIVE로 변경
        user.ifPresent(userRepository::save); // DB 저장
        return user;
    }

    /**
     * 사용자 승인 거부 처리
     */
    public Optional<User> rejectUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> u.setStatus("REJECTED")); // 상태 REJECTED로 변경
        user.ifPresent(userRepository::save);
        return user;
    }

    /**
     * 전체 사용자 조회 (관리자용)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
