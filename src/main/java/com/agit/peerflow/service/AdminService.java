package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.dto.user.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    User approveUser(Long userId);
    User rejectUser(Long userId);
    Page<User> getAllUsers(Pageable pageable);
    Page<User> getUsersByStatus(UserStatus status, Pageable pageable);
    Page<User> getUsersByRole(UserRole role, Pageable pageable);
    void updateUserByAdmin(Long userId, UserDTO.Request requestDTO);
    void resetPasswordByAdmin(Long userId, String newPassword);
    void deleteUserByAdmin(Long userId);
    int countTodayLoginAttempts(User user);
}