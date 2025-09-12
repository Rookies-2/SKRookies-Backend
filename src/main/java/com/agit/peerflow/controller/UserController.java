package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.dto.user.UserResponseDTO;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User API", description = "사용자 계정 관련 API (회원가입, 내 정보 관리 등)")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다. 가입 시 상태는 'PENDING'이 됩니다.")
    @PostMapping("/signup")
    public ResponseEntity<UserDTO.Response> signupUser(@RequestBody UserDTO.Request request) {
        User savedUser = userService.signup(request);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(savedUser));
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 상세 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserDTO.Response> getMyInfo(@AuthenticationPrincipal User user) {
        User myInfo = userService.getMyInfo(user.getUsername());
        return ResponseEntity.ok(UserDTO.Response.fromEntity(myInfo));

    }

    @Operation(summary = "내 정보 수정 (닉네임)", description = "현재 로그인된 사용자의 닉네임을 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<UserDTO.Response> updateMyInfo(
            @AuthenticationPrincipal User user,
            @RequestBody UserDTO.Request request) {
        User updatedUser = userService.updateMyInfo(user.getUsername(), request);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(updatedUser));
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 삭제합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal User user) {
        userService.deleteMyAccount(user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "비밀번호 변경", description = "현재 로그인된 사용자의 비밀번호를 변경합니다. 현재 비밀번호 확인이 필요합니다.")
    @PostMapping("/me/change-password")
    public ResponseEntity<UserDTO.Response> changePassword(
            @AuthenticationPrincipal User user,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        User updatedUser = userService.changePassword(user.getUsername(), oldPassword, newPassword);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(updatedUser));
    }

    // 사용자 ID로 프로필 이미지 업로드 (multipart/form-data)
    @PostMapping(
            value = "/{id}/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDTO.Response> uploadAvatarById(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            User updated = userService.uploadAvatarById(id, file);
            return ResponseEntity.ok(UserDTO.Response.fromEntity(updated));
        } catch (IllegalArgumentException e) {
            // 파일이 비었거나 잘못된 경우 등
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // 저장 실패 등 기타 오류
            return ResponseEntity.internalServerError().build();
        }
    }

    // 파일명으로 프로필 이미지 삭제
    @DeleteMapping(value = "/{id}/avatar/{fileName}")
    public ResponseEntity<UserDTO.Response> deleteAvatarById(
            @PathVariable Long id,
            @PathVariable String fileName
    ) {
        User updatedUser = userService.deleteAvatarById(id, fileName);

        return ResponseEntity.ok(UserDTO.Response.fromEntity(updatedUser));
    }
}
