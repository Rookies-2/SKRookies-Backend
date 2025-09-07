package com.agit.peerflow.controller;

import com.agit.peerflow.entity.User;
import com.agit.peerflow.entity.dto.UserDTO;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * 회원가입 요청
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO.Response> registerUser(@Valid @RequestBody UserDTO.Request request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            return ResponseEntity.badRequest().build();
        }

        User user = User.builder()
                .email(request.getEmail())
                .password("{noop}" + request.getPassword()) // 보안 적용 전: 단순 저장
                .username(request.getUsername())
                .nickname(request.getNickname())
                .role(request.getRole())
                .status("PENDING")
                .build();

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(UserDTO.Response.fromEntity(savedUser));
    }

    /**
     * 승인 대기 사용자 조회
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO.Response>> getPendingUsers() {
        List<UserDTO.Response> pendingUsers = userService.getPendingUsers()
                .stream()
                .map(UserDTO.Response::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingUsers);
    }

    /**
     * 회원 승인
     */
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO.Response> approveUser(@PathVariable Long id) {
        return userService.approveUser(id)
                .map(user -> ResponseEntity.ok(UserDTO.Response.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 회원 거부
     */
    @PutMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO.Response> rejectUser(@PathVariable Long id) {
        return userService.rejectUser(id)
                .map(user -> ResponseEntity.ok(UserDTO.Response.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 전체 사용자 조회 (관리자 전용)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO.Response>> getAllUsers() {
        List<UserDTO.Response> allUsers = userService.getAllUsers()
                .stream()
                .map(UserDTO.Response::fromEntity)
                .toList();

        return ResponseEntity.ok(allUsers);
    }

}
