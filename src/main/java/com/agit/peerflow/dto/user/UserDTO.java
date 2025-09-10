package com.agit.peerflow.dto.user;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class UserDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank
        private String userName;

        @NotBlank
        private String password;

        @NotBlank
        private String nickName;

        @NotBlank
        private String email;

        @NotBlank
        private UserRole role; // STUDENT, TEACHER
    }
    // ================= 비밀번호 변경 DTO =================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PasswordChangeRequest {
        @NotBlank
        private String currentPassword;

        @NotBlank
        private String newPassword;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PasswordResetRequest {
        @NotBlank
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PasswordUpdateRequest {
        @NotBlank
        private String token;

        @NotBlank
        private String newPassword;
    }

    // ================= Response DTO =================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String userName;
        private String nickName;
        private String email;
        private String role;
        private String status;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .userName(user.getUsername())
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .status(user.getStatus().name())
                    .build();
        }
    }
}
