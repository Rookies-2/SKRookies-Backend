package com.agit.peerflow.service.impl;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.LoginAttemptLogRepository;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service("adminServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptLogRepository loginAttemptLogRepository;
    @Override
    @Transactional
    public User approveUser(Long userId) {
        User user = findUserById(userId);
        user.approve();
        return user;
    }

    @Override
    @Transactional
    public User rejectUser(Long userId) {
        User user = findUserById(userId);
        user.reject();
        return user;
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> getUsersByStatus(UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<User> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    @Override
    @Transactional
    public void updateUserByAdmin(Long userId, UserDTO.Request requestDTO) {
        User user = findUserById(userId);

        // ✅ 개별 메서드를 사용하여 각 필드를 업데이트
        user.setUsername(requestDTO.getUsername());
        user.setNickname(requestDTO.getNickname());
    }

    @Override
    @Transactional
    public void resetPasswordByAdmin(Long userId, String newPassword) {
        User user = findUserById(userId);
        user.changePassword(passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public void deleteUserByAdmin(Long userId) {
        userRepository.deleteById(userId);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));
    }
    @Override
    @Transactional
    public int countTodayLoginAttempts(User user) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        // loginAttemptLogRepository를 사용하여 오늘 시도 횟수 반환
        return loginAttemptLogRepository.countTodayByUserEmail(user.getEmail(), start, end);
    }
}