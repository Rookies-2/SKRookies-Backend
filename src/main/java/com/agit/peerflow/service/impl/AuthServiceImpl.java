package com.agit.peerflow.service.Impl;

import com.agit.peerflow.ai.AiClient;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.security.component.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AiClient aiClient;

    public String login(UserDTO.Request requestDTO, HttpServletRequest request) {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "User", "email", requestDTO.getEmail()));

        // 1️⃣ 비밀번호 검증
        boolean success = passwordEncoder.matches(requestDTO.getPassword(), user.getPassword());
        if (!success) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Password", "password", "불일치");
        }

        // 2️⃣ AI 판단
        Map<String, Object> features = Map.of(
                "email", user.getEmail(),
                "ip", request.getRemoteAddr(),
                "device", request.getHeader("User-Agent")
        );

        boolean blocked = aiClient.checkBlocked(features);
        if (blocked) {
            throw new BusinessException(ErrorCode.AI_BLOCKED, "로그인", "AI 판단", "차단됨");
        }

        // 3️⃣ JWT 토큰 생성
        Map<String, Object> claims = Map.of("role", user.getRole().name());
        return jwtTokenProvider.createToken(user.getEmail(), claims);
    }
}