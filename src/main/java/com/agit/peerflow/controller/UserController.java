package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.UserDTO;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    // 회원가입 (학생/선생님)
    @PostMapping("/signup")
    public ResponseEntity<UserDTO.Response> signupUser(@RequestBody UserDTO.Request request) {
        User savedUser = userService.signupUser(request);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(savedUser));
    }

    // 본인 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserDTO.Response> getMyInfo(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
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
}
