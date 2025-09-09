package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<Page<UserDTO.Response>> getAllUsers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<User> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users.map(UserDTO.Response::fromEntity));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<UserDTO.Response>> getUsersByStatus(
            @PathVariable String status, Pageable pageable) {
        Page<User> users = adminService.getUsersByStatus(UserStatus.valueOf(status.toUpperCase()), pageable);
        return ResponseEntity.ok(users.map(UserDTO.Response::fromEntity));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<Page<UserDTO.Response>> getUsersByRole(
            @PathVariable String role, Pageable pageable) {
        Page<User> users = adminService.getUsersByRole(UserRole.valueOf(role.toUpperCase()), pageable);
        return ResponseEntity.ok(users.map(UserDTO.Response::fromEntity));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<UserDTO.Response> approveUser(@PathVariable Long id) {
        User approvedUser = adminService.approveUser(id);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(approvedUser));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<UserDTO.Response> rejectUser(@PathVariable Long id) {
        User rejectedUser = adminService.rejectUser(id);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(rejectedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUserByAdmin(@PathVariable Long id, @RequestBody UserDTO.Request request) {
        adminService.updateUserByAdmin(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        adminService.resetPasswordByAdmin(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUserByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}