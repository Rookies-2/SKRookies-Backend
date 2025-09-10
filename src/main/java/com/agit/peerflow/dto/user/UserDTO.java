package com.agit.peerflow.dto.user;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDTO {

    /**
     * 회원가입, 정보 수정 등 '요청'에 사용되는 DTO
     */
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

        private UserRole role;
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

    /**
     * 사용자 정보 '응답'에 사용되는 DTO
     */
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

        private Long id;
        private String userName;
        private String nickName;
        private String email;
        private String role;
        private String status;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .id(user.getId())
                    .userName(user.getUsername())
                    .nickName(user.getNickname())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .status(user.getStatus().name())
                    .build();
        }
    }
}