package com.agit.peerflow.security.controller;

import com.agit.peerflow.domain.entity.LoginAttemptLog;
import com.agit.peerflow.domain.entity.PasswordResetLog;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.repository.LoginAttemptLogRepository;
import com.agit.peerflow.repository.PasswordResetLogRepository;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.security.component.JwtTokenProvider;
import com.agit.peerflow.service.AuthService;
import com.agit.peerflow.service.MailService;
import com.agit.peerflow.service.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordService passwordService; // 새 Service로 분리

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO.Request request, HttpServletRequest httpRequest) {
        String token = authService.login(request, httpRequest);
        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email) {
        passwordService.sendVerificationCode(email);
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    @PostMapping("/password/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> body) {
        passwordService.verifyCode(body.get("email"), body.get("code"));
        return ResponseEntity.ok("인증번호 확인 완료");
    }

    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        passwordService.updatePassword(body.get("email"), body.get("code"), body.get("newPassword"), request);
        return ResponseEntity.ok("✅ 비밀번호가 정상적으로 변경되었습니다.");
    }
}