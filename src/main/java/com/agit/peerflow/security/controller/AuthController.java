package com.agit.peerflow.security.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.security.component.JwtTokenProvider;
import com.agit.peerflow.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MailService mailService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO.Request request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("userRole", user.getRole());

        // 토큰의 주체(subject)를 username으로 생성합니다.
        String token = jwtTokenProvider.createToken(user.getEmail(), extraClaims);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);

        return ResponseEntity.ok(response);
    }

    // 1️⃣ 인증번호 발송
    @PostMapping("/password/reset")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email) {
        mailService.sendVerificationCode(email);
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    // 2️⃣ 인증번호 검증
    @PostMapping("/password/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            return ResponseEntity.badRequest().body("인증번호가 올바르지 않습니다.");
        }

        if (user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("인증번호가 만료되었습니다.");
        }

        return ResponseEntity.ok("인증번호 확인 완료");
    }

    // 3️⃣ 새 비밀번호 저장 (MailService 사용)
    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code"); // 인증번호 필요
        String newPassword = body.get("newPassword");

        // MailService로 검증 + 비밀번호 변경
        mailService.resetPasswordByCode(email, code, newPassword);

        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }


}