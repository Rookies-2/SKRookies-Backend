package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.UserDTO;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository; // (안 쓰면 지워도 됨)

    // 회원가입 (학생/선생님)
    @PostMapping("/signup")
    public ResponseEntity<UserDTO.Response> signupUser(@RequestBody UserDTO.Request request) {
        User savedUser = userService.signupUser(request);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(savedUser));
    }

    // 본인 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserDTO.Response> getMyInfo(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername()); // JWT에서 추출한 email
        return ResponseEntity.ok(UserDTO.Response.fromEntity(user));
    }

    // 본인 정보 수정
    @PutMapping("/me")
    public ResponseEntity<UserDTO.Response> updateMyInfo(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestBody UserDTO.Request request) {

        // JWT에서 추출한 이메일로 사용자 조회 후 수정
        User updatedUser = userService.updateUserByEmail(userDetails.getUsername(), request);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(updatedUser));
    }

    // 본인 계정 삭제
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        // JWT에서 추출한 이메일로 사용자 조회 후 삭제
        userService.deleteUserByEmail(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // [추가] 사용자 ID로 프로필 이미지 업로드 (multipart/form-data)
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
}

