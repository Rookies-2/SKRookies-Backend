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
        private String username;

        @NotBlank
        private String password;

        @NotBlank
        private String nickname;

        @NotBlank
        private String email;

        private UserRole role;
    }

    /**
     * 사용자 정보 '응답'에 사용되는 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String role;
        private String status;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .status(user.getStatus().name())
                    .build();
        }
    }
}