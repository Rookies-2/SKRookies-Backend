package com.agit.peerflow.security.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.security.component.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider; // JwtService 대신 JwtTokenProvider로 변경
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO.Request request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // `creator_id`가 null이 되지 않도록 user.getId()를 추가합니다.
        Map<String, Object> extraClaims = Map.of(
                "userId", user.getId(),
                "userRole", user.getRole()
        );

        // 올바른 코드: getEmail()을 호출하여 토큰 생성
        String token = jwtTokenProvider.createToken(user.getEmail(), extraClaims);

        return ResponseEntity.ok().body(token);
    }
}