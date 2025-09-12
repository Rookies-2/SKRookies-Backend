package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    String login(UserDTO.Request requestDTO, HttpServletRequest request);
}