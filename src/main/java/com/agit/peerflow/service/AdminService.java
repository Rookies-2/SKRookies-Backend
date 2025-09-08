package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 사용자 승인
    @Transactional
    public User approveUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));
        user.setStatus(UserStatus.ACTIVE);
        user.setApprovedAt(LocalDateTime.now());
        // 후처리 (알림, 히스토리 기록)
        // notificationService.sendUserApproved(user);
        // historyService.recordApproval(user);

        return userRepository.save(user);
    }

    // 사용자 거부
    @Transactional
    public User rejectUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));

        user.setStatus(UserStatus.REJECTED);
        // 후처리 (알림, 히스토리 기록)
        // notificationService.sendUserRejected(user);
        // historyService.recordRejection(user);
        return userRepository.save(user);
    }
    // 승인 대기 사용자 조회
    public List<User> getPendingUsers() {

        return userRepository.findByStatus(UserStatus.PENDING);
    }

    // 전체 사용자 조회
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
    // 관리자용 사용자 비밀번호 초기화 (본인 인증 없음, 사용자 email 기준)
    public User resetPasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
