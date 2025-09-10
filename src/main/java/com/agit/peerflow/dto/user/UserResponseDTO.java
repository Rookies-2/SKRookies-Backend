package com.agit.peerflow.dto.user;

import com.agit.peerflow.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

<<<<<<< HEAD
@Getter
@Builder
public class UserResponseDTO {
    private final Long id;
    private final String username;
    private final String nickname;
    private final String email;
    private final String role;
    private final String status;

    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
=======
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
>>>>>>> f6a98f1fa00588fe08cfc97f653c4ca10eb2e422
    }
}