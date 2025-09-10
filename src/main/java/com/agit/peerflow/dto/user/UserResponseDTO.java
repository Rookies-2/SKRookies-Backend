package com.agit.peerflow.dto.user;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;

public record UserResponseDTO(
        Long id,
        String userName,
        String nickName,
        UserRole role,
        UserStatus status
) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getNickName(),
                user.getRole(),
                user.getStatus()
        );
    }
}
