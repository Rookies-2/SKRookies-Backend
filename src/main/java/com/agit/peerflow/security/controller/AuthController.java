package com.agit.peerflow.security.controller;

import com.agit.peerflow.dto.auth.LoginRequestDto;
import com.agit.peerflow.dto.user.PasswordDTO;
import com.agit.peerflow.service.LoginService;
import com.agit.peerflow.service.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto,
                                   HttpServletRequest httpRequest) {
        return loginService.login(requestDto, httpRequest);
    }

    // ================== 비밀번호 재설정 ==================
    @PostMapping("/password/reset")
    public ResponseEntity<?> sendVerificationCode(@RequestBody PasswordDTO.ResetRequest request,
                                                  HttpServletRequest httpRequest) {
        passwordService.sendVerificationCode(request, httpRequest);
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    @PostMapping("/password/verify")
    public ResponseEntity<?> verifyCode(@RequestBody PasswordDTO.VerifyRequest request) {
        passwordService.verifyCode(request);
        return ResponseEntity.ok("인증번호 확인 완료");
    }

    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordDTO.UpdateRequest request,
                                            HttpServletRequest httpRequest) {
        passwordService.updatePassword(request, httpRequest);
        return ResponseEntity.ok("✅ 비밀번호가 정상적으로 변경되었습니다.");
    }
}
