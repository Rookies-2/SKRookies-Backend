package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;


    // 사용자 승인
    public User approveUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + id));

        // 상태 변경
        user.changeStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    // 사용자 거부
    public Optional<User> rejectUser(Long id) {
        return userRepository.findById(id).map(user -> {
            user.changeStatus(UserStatus.REJECTED);
            return userRepository.save(user);
        });
    }
    // 승인 대기 사용자 조회
    public List<User> getPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING);
    }

    // 전체 사용자 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
