package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
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

    @PostMapping("/signup")
    public ResponseEntity<UserDTO.Response> signupUser(@RequestBody UserDTO.Request request) {
        User savedUser = userService.signup(request);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(savedUser));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO.Response> getMyInfo(@AuthenticationPrincipal User user) {
        // user.getUsername()은 JWT의 'sub' 값인 이메일을 반환
        User myInfo = userService.getMyInfo(user.getUsername());
        return ResponseEntity.ok(UserDTO.Response.fromEntity(myInfo));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateMyInfo(
            @AuthenticationPrincipal User user,
            @RequestBody UserDTO.Request request) {
        userService.updateMyInfo(user.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal User user) {
        userService.deleteMyAccount(user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User user,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(user.getUsername(), oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }
}