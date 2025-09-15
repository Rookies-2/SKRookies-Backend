package com.agit.peerflow.dto.message;

import com.agit.peerflow.domain.entity.User;

public record SenderDTO(
        Long id,
        String username,
        String nickname,
        String email,
        String avatarUrl
) {
    public static SenderDTO from(User user) {
        return new SenderDTO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getAvatarUrl()
        );
    }
}

