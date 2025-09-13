package com.agit.peerflow.service;

import com.agit.peerflow.ai.AiClient;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.PasswordDTO;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.security.service.PacketCaptureService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PasswordService {

    private final UserRepository userRepository;
    private final AiClient aiClient;
    private final MailService mailService;
    private final PacketCaptureService packetCaptureService;

    // ================== 인증번호 발송 ==================
    public void sendVerificationCode(PasswordDTO.ResetRequest request, HttpServletRequest httpRequest) {
        try {
            // 1️⃣ 패킷 특성 추출
            Map<String, Object> features;
            try {
                features = packetCaptureService.captureFeatures();
            } catch (Exception e) {
                log.warn("⚠️ 패킷 캡처 실패, 기본값으로 대체", e);
                features = new HashMap<>();
                features.put("dur", 0.0);
                features.put("proto", "tcp");
                features.put("service", "-");
                features.put("state", "INT");
            }

            // 2️⃣ 이메일/IP/Device 정보 추가
            features.put("email", request.getEmail());
            features.put("ip", httpRequest.getRemoteAddr());
            features.put("device", httpRequest.getHeader("User-Agent"));

            // 3️⃣ AI 판단
            boolean blocked = aiClient.checkBlocked(features);
            if (blocked) {
                log.warn("🚨 비정상적인 비밀번호 재설정 시도로 차단됨: email={}", request.getEmail());
                throw new RuntimeException("🚨 비정상적인 비밀번호 재설정 시도로 차단되었습니다.");
            }

            // 4️⃣ 인증번호 발송
            mailService.sendVerificationCode(request.getEmail());
            log.info("✅ 인증번호 발송 성공: email={}", request.getEmail());

        } catch (Exception e) {
            log.error("❌ 인증번호 발송 실패: email={}", request.getEmail(), e);
            throw e;
        }
    }

    // ================== 인증번호 확인 ==================
    public void verifyCode(PasswordDTO.VerifyRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getCode().equals(user.getVerificationCode()) ||
                user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증번호가 올바르지 않거나 만료되었습니다.");
        }
        log.info("✅ 인증번호 확인 성공: email={}", request.getEmail());
    }

    // ================== 비밀번호 변경 ==================
    public void updatePassword(PasswordDTO.UpdateRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1️⃣ 패킷 특성 추출
        Map<String, Object> features;
        try {
            features = packetCaptureService.captureFeatures();
        } catch (Exception e) {
            log.warn("⚠️ 패킷 캡처 실패, 기본값으로 대체", e);
            features = new HashMap<>();
            features.put("dur", 0.0);
            features.put("proto", "tcp");
            features.put("service", "-");
            features.put("state", "INT");
        }

        features.put("email", user.getEmail());
        features.put("ip", httpRequest.getRemoteAddr());
        features.put("device", httpRequest.getHeader("User-Agent"));

        // 2️⃣ AI 판단
        boolean blocked = aiClient.checkBlocked(features);
        if (blocked) {
            log.warn("🚨 비정상적인 비밀번호 재설정 시도로 차단됨: email={}", user.getEmail());
            throw new RuntimeException("🚨 비정상적인 비밀번호 재설정 시도로 차단되었습니다.");
        }

        // 3️⃣ 비밀번호 변경
        mailService.resetPasswordByCode(request.getEmail(), request.getCode(), request.getNewPassword());
        log.info("✅ 비밀번호 변경 성공: email={}", user.getEmail());
    }
}