package com.agit.peerflow.security.controller;

import com.agit.peerflow.dto.auth.LoginRequestDto;
import com.agit.peerflow.dto.user.PasswordDTO;
import com.agit.peerflow.service.LoginService;
import com.agit.peerflow.service.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final PasswordService passwordService;

    // ================== 로그인 ==================
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto requestDto,
                                   HttpServletRequest httpRequest) {
        return loginService.login(requestDto, httpRequest);
    }

    // ================== 비밀번호 재설정 ==================
    @PostMapping("/password/reset")
    public ResponseEntity<?> sendVerificationCode(@Valid @RequestBody PasswordDTO.ResetRequest request,
                                                  HttpServletRequest httpRequest) {

        return passwordService.sendVerificationCode(request, httpRequest);
    }

    @PostMapping("/password/verify")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody PasswordDTO.VerifyRequest request) {

        return passwordService.verifyCode(request);
    }

    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordDTO.UpdateRequest request,
                                            HttpServletRequest httpRequest) {

        return passwordService.updatePassword(request, httpRequest);
    }
}
