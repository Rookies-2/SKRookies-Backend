package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.UserDTO;
import com.agit.peerflow.service.AdminService;
import com.agit.peerflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    // 모든 사용자 조회
    @GetMapping
    public ResponseEntity<List<UserDTO.Response>> getAllUsers() {
        List<UserDTO.Response> users = adminService.getAllUsers()
                .stream()
                .map(UserDTO.Response::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // 사용자 승인 대기 조회
    @GetMapping("/pending")
    public ResponseEntity<List<UserDTO.Response>> getPendingUsers() {
        List<UserDTO.Response> pendingUsers = adminService.getPendingUsers()
                .stream()
                .map(UserDTO.Response::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingUsers);
    }

    // 사용자 승인 (PENDING -> ACTIVE)
    @PutMapping("/{id}/approve")
    public ResponseEntity<UserDTO.Response> approveUser(@PathVariable Long id) {
        User approvedUser = adminService.approveUserById(id);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(approvedUser));
    }

    // 사용자 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO.Response> updateUser(@PathVariable Long id, @RequestBody UserDTO.Request request) {
        User updatedUser = userService.updateUserById(id, request);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(updatedUser));
    }
    // 관리자 권한으로 비밀번호 초기화
    @PostMapping("/{email}/reset-password")
    public ResponseEntity<User> resetPassword(
            @PathVariable String email,
            @RequestParam String newPassword) {
        return ResponseEntity.ok(adminService.resetPasswordByEmail(email, newPassword));
    }

    // 사용자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
