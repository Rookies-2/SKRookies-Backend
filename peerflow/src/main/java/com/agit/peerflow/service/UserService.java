package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;

public interface UserService {
    User getByUsername(String username);
    User register(String username, String rawPassword, String nickname, String email, String profileImageUrl);
    User signupUser(UserDTO.Request request);
    User getUserByEmail(String email);
    User updateUserByEmail(String email, UserDTO.Request request);
    User updateUserById(Long id, UserDTO.Request request);
    void deleteUserByEmail(String email);
    User getUserById(Long id);
    void deleteUserById(Long id);
}
