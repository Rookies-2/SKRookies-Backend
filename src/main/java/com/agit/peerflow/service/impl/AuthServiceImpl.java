package com.agit.peerflow.service.Impl;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.security.component.JwtTokenProvider;
import com.agit.peerflow.service.AiSecurityService;
import com.agit.peerflow.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AiSecurityService aiSecurityService;

    @Override
    public String login(UserDTO.Request requestDTO, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "User", "email", requestDTO.getEmail()));

        boolean success = passwordEncoder.matches(requestDTO.getPassword(), user.getPassword());

        // 오늘 로그인 시도 횟수 계산
        int attempts = aiSecurityService.countTodayLoginAttempts(user);

        String ip = httpRequest.getRemoteAddr();
        String device = httpRequest.getHeader("User-Agent");

        // AI 판단
        if (aiSecurityService.checkLoginAttempt(user, attempts + 1, ip, device)) {
            throw new BusinessException(ErrorCode.AI_BLOCKED, "로그인", "AI 판단", "차단됨");
        }

        if (!success) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Password", "password", "불일치");
        }

        // JWT 발급
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        return jwtTokenProvider.createToken(user.getEmail(), claims);
    }

}
