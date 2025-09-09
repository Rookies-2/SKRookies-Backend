package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

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
    public User approveUserById(@PathVariable Long id) {
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
    public User rejectUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));

        user.setStatus(UserStatus.REJECTED);
        // 후처리 (알림, 히스토리 기록)
        // notificationService.sendUserRejected(user);
        // historyService.recordRejection(user);
        return userRepository.save(user);
    }
    // 승인 대기 사용자 조회
    public Page<User> getPendingUsers(Pageable pageable) {

        return userRepository.findByStatus(UserStatus.PENDING, pageable);
    }

    // 전체 사용자 조회
    public Page<User> getAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable);
    }
    // 상태별 사용자 페이징 조회
    public Page<User> getUsersByStatus(UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    // 역할별 사용자 페이징 조회
    public Page<User> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    // 관리자용 사용자 비밀번호 초기화 (본인 인증 없음, 사용자 email 기준)
    public User resetPasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
