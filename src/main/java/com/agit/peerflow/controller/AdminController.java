package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin API", description = "관리자 기능 관련 API (사용자 관리 등)")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "전체 사용자 목록 조회", description = "모든 사용자를 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<UserDTO.Response>> getAllUsers(Pageable pageable) {
        Page<User> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users.map(UserDTO.Response::fromEntity));
    }

    @Operation(summary = "상태별 사용자 목록 조회", description = "특정 상태(PENDING, ACTIVE 등)의 사용자를 페이징하여 조회합니다.")
    @Parameter(name = "status", description = "조회할 사용자 상태 (PENDING, ACTIVE, REJECTED 등)", example = "PENDING")
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<UserDTO.Response>> getUsersByStatus(@PathVariable String status, Pageable pageable) {
        Page<User> users = adminService.getUsersByStatus(UserStatus.valueOf(status.toUpperCase()), pageable);
        return ResponseEntity.ok(users.map(UserDTO.Response::fromEntity));
    }

    @Operation(summary = "역할별 사용자 목록 조회", description = "특정 역할(STUDENT, TEACHER 등)의 사용자를 페이징하여 조회합니다.")
    @Parameter(name = "role", description = "조회할 사용자 역할 (STUDENT, TEACHER, ADMIN)", example = "STUDENT")
    @GetMapping("/role/{role}")
    public ResponseEntity<Page<UserDTO.Response>> getUsersByRole(@PathVariable String role, Pageable pageable) {
        Page<User> users = adminService.getUsersByRole(UserRole.valueOf(role.toUpperCase()), pageable);
        return ResponseEntity.ok(users.map(UserDTO.Response::fromEntity));
    }

    @Operation(summary = "사용자 가입 승인", description = "대기 중인 사용자를 활성 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/{id}/approve")
    public ResponseEntity<UserDTO.Response> approveUser(@Parameter(description = "승인할 사용자 ID") @PathVariable Long id) {
        User approvedUser = adminService.approveUser(id);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(approvedUser));
    }

    @Operation(summary = "사용자 가입 거절", description = "대기 중인 사용자를 거절 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "거절 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/{id}/reject")
    public ResponseEntity<UserDTO.Response> rejectUser(@Parameter(description = "거절할 사용자 ID") @PathVariable Long id) {
        User rejectedUser = adminService.rejectUser(id);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(rejectedUser));
    }

    @Operation(summary = "사용자 정보 수정 (관리자용)", description = "관리자가 특정 사용자의 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUserByAdmin(@Parameter(description = "수정할 사용자 ID") @PathVariable Long id, @RequestBody UserDTO.Request request) {
        adminService.updateUserByAdmin(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 비밀번호 초기화", description = "관리자가 특정 사용자의 비밀번호를 강제로 초기화합니다.")
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "비밀번호를 초기화할 사용자 ID") @PathVariable Long id,
            @Parameter(description = "새로운 비밀번호") @RequestParam String newPassword) {
        adminService.resetPasswordByAdmin(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 삭제", description = "관리자가 특정 사용자를 시스템에서 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "삭제할 사용자 ID") @PathVariable Long id) {
        adminService.deleteUserByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}