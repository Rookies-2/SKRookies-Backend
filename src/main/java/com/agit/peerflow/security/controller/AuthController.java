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
    @PostMapping("/password/reset")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        mailService.sendPasswordResetToken(email);
        return ResponseEntity.ok("Password reset link has been sent.");
    }
    @GetMapping("/password/update")
    public ResponseEntity<?> showResetForm(@RequestParam String token) {
        // 토큰 검증
        Optional<User> optionalUser = userRepository.findByPasswordResetToken(token);
        if(optionalUser.isEmpty() || optionalUser.get().getPasswordResetTokenExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        // 토큰 유효 → 프론트에서 폼 보여주기
        // API만 있다면 token을 그대로 프론트로 전달
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        mailService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been updated.");
    }


}