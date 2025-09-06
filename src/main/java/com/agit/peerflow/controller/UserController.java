package com.agit.peerflow.controller;

import com.agit.peerflow.entity.User;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User 관련 API Controller
 * - 회원가입, 승인, 거부, 조회
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private  final UserRepository userRepository;
    /**
     * 회원가입 요청
     * - status = PENDING
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        // 이메일, 닉네임 유니크 체크 (간단하게)
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        if (userRepository.existsByNickname(user.getNickname())) {
            return ResponseEntity.badRequest().build();
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * 승인 대기 사용자 조회
     */
    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(userService.getPendingUsers());
    }

    /**
     * 회원 승인
     */
    @PutMapping("/approve/{id}")
    public ResponseEntity<User> approveUser(@PathVariable Long id) {
        return userService.approveUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 회원 거부
     */
    @PutMapping("/reject/{id}")
    public ResponseEntity<User> rejectUser(@PathVariable Long id) {
        return userService.rejectUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 전체 사용자 조회 (관리자용)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
