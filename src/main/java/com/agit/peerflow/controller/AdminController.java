package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.UserDTO;
import com.agit.peerflow.service.AdminService;
import com.agit.peerflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    // 모든 사용자 조회(페이징 + 정렬)
    @GetMapping
    public ResponseEntity<Page<UserDTO.Response>> getAllUsers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<User> users = adminService.getAllUsers(pageable); // DB에서 Page<User> 조회
        Page<UserDTO.Response> dtoPage = users.map(UserDTO.Response::fromEntity); // DTO 변환

        return ResponseEntity.ok(dtoPage);
    }//getAllUsers

    // 사용자 승인 상태 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<UserDTO.Response>> getUsersByStatus(
            @PathVariable("status") String status,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<User> users  = adminService.getUsersByStatus(
                Enum.valueOf(com.agit.peerflow.domain.enums.UserStatus.class, status.toUpperCase()),
                pageable
        ); // DB에서 Page<User> 상태별 조회

        Page<UserDTO.Response> dtoPage = users.map(UserDTO.Response::fromEntity); // DTO 변환
        return ResponseEntity.ok(dtoPage);
    }//getUsersByStatus

    // 역할별 사용자 조회 (예: ADMIN, STUDENT)
    @GetMapping("/role/{role}")
    public ResponseEntity<Page<UserDTO.Response>> getUsersByRole(
            @PathVariable("role") String role,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<User> users = adminService.getUsersByRole(
                Enum.valueOf(com.agit.peerflow.domain.enums.UserRole.class, role.toUpperCase()),
                pageable
        );

        Page<UserDTO.Response> dtoPage = users.map(UserDTO.Response::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }//getUsersByRole

    // 사용자 승인 (PENDING -> ACTIVE)
    @PutMapping("/{id}/approve")
    public ResponseEntity<UserDTO.Response> approveUser(@PathVariable Long id) {
        User approvedUser = adminService.approveUserById(id);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(approvedUser));
    }//approveUser

    // 사용자 승인 거부 (PENDING -> REJECTED)
    @PutMapping("/{id}/reject")
    public ResponseEntity<UserDTO.Response> rejectUser(@PathVariable Long id) {
        User rejectedUser = adminService.rejectUser(id);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(rejectedUser));
    }//rejectUser

    // 사용자 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO.Response> updateUser(@PathVariable Long id, @RequestBody UserDTO.Request request) {
        User updatedUser = userService.updateUserById(id, request);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(updatedUser));
    }//updateUser

    // 관리자 권한으로 비밀번호 초기화
    @PostMapping("/{email}/reset-password")
    public ResponseEntity<User> resetPassword(
            @PathVariable String email,
            @RequestParam String newPassword) {
        return ResponseEntity.ok(adminService.resetPasswordByEmail(email, newPassword));
    }//resetPassword

    // 사용자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }//deleteUser
}
