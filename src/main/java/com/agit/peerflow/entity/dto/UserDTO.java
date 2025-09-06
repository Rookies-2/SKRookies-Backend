package com.agit.peerflow.entity.dto;


import com.agit.peerflow.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class UserDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "User Email is required")
        private String email;

        @NotBlank(message = "User name is required")
        @Size(max = 100, message = "User name cannot exceed 100 characters")
        private String username;

        @NotBlank(message = "User Password is required")
        @Size(max = 20, message = "User Password cannot exceed 20 characters")
        private String password;

        @NotBlank(message = "User Nickname is required")
        @Size(max = 10, message = "User Nickname cannot exceed 10 characters")
        private String nickname;

        @NotBlank(message = "Are you a Student or a Teacher??")
        private String role;

    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String email;
        private String username;
        private String nickname;
        private String role;


        public static Response fromEntity(User user) {

            return Response.builder()
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .role(user.getRole())
                    .build();
        }
    }


}