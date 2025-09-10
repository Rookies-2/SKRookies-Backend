package com.agit.peerflow.dto;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
                    .userName(user.getUserName())
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .status(user.getStatus().name())
                    .build();
        }
    }
}
