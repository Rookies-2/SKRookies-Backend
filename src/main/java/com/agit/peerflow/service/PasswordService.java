package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.repository.LoginAttemptLogRepository;
import com.agit.peerflow.repository.PasswordResetLogRepository;
import com.agit.peerflow.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetLogRepository resetLogRepository;
    private final AiSecurityService aiSecurityService;
    private final LoginAttemptLogRepository loginAttemptLogRepository;
    public void sendVerificationCode(String email) {
        mailService.sendVerificationCode(email);
    }

    public void verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!code.equals(user.getVerificationCode()) || user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증번호가 올바르지 않거나 만료됨");
        }
    }

    public void updatePassword(String email, String code, String newPassword, HttpServletRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // AI + 로그 기록
        int attempts = aiSecurityService.countTodayResetAttempts(user);
        String ip = request.getRemoteAddr();
        String device = request.getHeader("User-Agent");

        if (aiSecurityService.checkResetAttempt(user, attempts, ip, device)) {
            throw new RuntimeException("🚨 비정상적인 비밀번호 재설정 시도로 차단되었습니다.");
        }

        mailService.resetPasswordByCode(email, code, newPassword);
    }
}
