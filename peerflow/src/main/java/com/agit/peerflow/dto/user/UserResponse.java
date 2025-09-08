package com.agit.peerflow.dto.user;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;

public record UserResponse(
        Long id,
        String username,
        String nickname,
        UserRole role,
        UserStatus status
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole(),
                user.getStatus()
        );
    }
}
