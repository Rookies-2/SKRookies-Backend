package com.agit.peerflow.service;

import com.agit.peerflow.ai.AiClient;
import com.agit.peerflow.domain.entity.PasswordResetLog;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.PasswordDTO;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.PasswordResetLogRepository;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.security.service.PacketCaptureService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PasswordService {

    private final UserRepository userRepository;
    private final AiClient aiClient;
    private final MailService mailService;
    private final PacketCaptureService packetCaptureService;
    private final PasswordResetLogRepository passwordResetLogRepository;
    private static final int MAX_RESET_ATTEMPTS = 5;

    // ================== 인증번호 발송 ==================
    public ResponseEntity<?> sendVerificationCode(PasswordDTO.ResetRequest request, HttpServletRequest httpRequest) {
        try {
            // 오늘 비밀번호 재설정 시도 횟수 조회
            int todayAttempts = passwordResetLogRepository.countTodayByEmail(
                    request.getEmail(),
                    LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN),
                    LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX)
            );

            if (todayAttempts >= MAX_RESET_ATTEMPTS) {
                log.warn("❌ 비밀번호 재설정 시도 횟수 초과: email={}", request.getEmail());
                throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "비밀번호 재설정 시도 횟수를 초과했습니다.");
            }

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "User", "email", request.getEmail()));
            // 패킷 특성 추출
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
            boolean isBlocked = aiClient.checkBlocked(features);

            PasswordResetLog logEntry = PasswordResetLog.builder()
                    .user(user)
                    .email(request.getEmail())
                    .ip(httpRequest.getRemoteAddr())
                    .device(httpRequest.getHeader("User-Agent"))
                    .aiBlocked(isBlocked)
                    .attempts(todayAttempts + 1)
                    .build();
            passwordResetLogRepository.save(logEntry);

            // AI 판단
            if (isBlocked) {
                log.warn("🚫 AI에 의해 비밀번호 재설정 시도 차단됨: email={}", request.getEmail());
                String message = ErrorCode.AI_BLOCKED.formatMessage("비밀번호 재설정", user.getEmail());
                throw new BusinessException(ErrorCode.AI_BLOCKED, "비밀번호 재설정", user.getEmail());
            }

            // 인증번호 발송
            mailService.sendVerificationCode(request.getEmail());
            log.info("✅ 인증번호 발송 성공: email={}", request.getEmail());
            return ResponseEntity.ok(Map.of("message", "✅ 인증 코드가 이메일로 전송되었습니다."));

        } catch (Exception e) {
            log.error("❌ 인증번호 발송 실패: email={}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "❌ 인증번호 발송 중 오류가 발생했습니다."));
        }
    }

    // ================== 인증번호 확인 ==================
    public ResponseEntity<?> verifyCode(PasswordDTO.VerifyRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "User", "email", request.getEmail()));

            if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 인증 코드입니다.");
            }

            if (user.getVerificationCodeExpiration() == null || user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "인증 코드가 만료되었습니다.");
            }

        return ResponseEntity.ok(Map.of("message", "인증 코드가 확인되었습니다."));


        } catch (Exception e) {
            log.error("❌ 인증번호 확인 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "❌ 인증번호 확인 중 서버 오류가 발생했습니다."));
        }
    }

    // ================== 비밀번호 변경 ==================
    public ResponseEntity<?> updatePassword(PasswordDTO.UpdateRequest request, HttpServletRequest httpRequest) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "User", "email", request.getEmail()));

//            // 패킷 특성 추출 (테스트용)
            Map<String, Object> features = new HashMap<>();
//            // 패스워드 변경 성공 예시
//                    features.put("dur", 0.000011);
//                    features.put("proto", "udp");
//                    features.put("service", "-");
//                    features.put("state", "INT");
//                    features.put("spkts", 2);
//                    features.put("dpkts", 0);
//                    features.put("sbytes", 496);
//                    features.put("dbytes", 0);
//                    features.put("rate", 90909.0902);
//                    features.put("sttl", 254);
//                    features.put("dttl", 0);
//                    features.put("sload", 180363632);
//                    features.put("dload", 0);
//                    features.put("sloss", 0);
//                    features.put("dloss", 0);
//                    features.put("sinpkt", 0.011);
//                    features.put("dinpkt", 0);
//                    features.put("sjit", 0);
//                    features.put("djit", 0);
//                    features.put("swin", 0);
//                    features.put("dwin", 0);
//                    features.put("tcprtt", 0);
//                    features.put("synack", 0);
//                    features.put("ackdat", 0);
//                    features.put("smean", 248);
//                    features.put("dmean", 0);
//                    features.put("trans_depth", 0);
//                    features.put("response_body_len", 0);
//                    features.put("ct_srv_src", 2);
//                    features.put("ct_state_ttl", 2);
//                    features.put("ct_dst_ltm", 1);
//                    features.put("ct_src_dport_ltm", 1);
//                    features.put("ct_dst_sport_ltm", 1);
//                    features.put("ct_dst_src_ltm", 2);
//                    features.put("is_ftp_login", 0);
//                    features.put("ct_ftp_cmd", 0);
//                    features.put("ct_flw_http_mthd", 0);
//                    features.put("ct_src_ltm", 1);
//                    features.put("ct_srv_dst", 2);
//                    features.put("is_sm_ips_ports", 0);
//            // 패스워드 변경 차단 예시
            features.put("dur", 0.921987);
            features.put("proto", "ospf");
            features.put("service", "-");
            features.put("state", "INT");
            features.put("spkts", 20);
            features.put("dpkts", 0);
            features.put("sbytes", 1280);
            features.put("dbytes", 0);
            features.put("rate", 20.607666);
            features.put("sttl", 254);
            features.put("dttl", 0);
            features.put("sload", 10551.125);
            features.put("dload", 0);
            features.put("sloss", 0);
            features.put("dloss", 0);
            features.put("sinpkt", 48.525633);
            features.put("dinpkt", 0);
            features.put("sjit", 52.253805);
            features.put("djit", 0);
            features.put("swin", 0);
            features.put("dwin", 0);
            features.put("tcprtt", 0);
            features.put("synack", 0);
            features.put("ackdat", 0);
            features.put("smean", 64);
            features.put("dmean", 0);
            features.put("trans_depth", 0);
            features.put("response_body_len", 0);
            features.put("ct_srv_src", 1);
            features.put("ct_state_ttl", 2);
            features.put("ct_dst_ltm", 1);
            features.put("ct_src_dport_ltm", 1);
            features.put("ct_dst_sport_ltm", 1);
            features.put("ct_dst_src_ltm", 2);
            features.put("is_ftp_login", 0);
            features.put("ct_ftp_cmd", 0);
            features.put("ct_flw_http_mthd", 0);
            features.put("ct_src_ltm", 1);
            features.put("ct_srv_dst", 1);
            features.put("is_sm_ips_ports", 0);

            //실제 네트워크 트래픽용 패킷 특성 추출
//        Map<String, Object> features;
//        try {
//            features = packetCaptureService.captureFeatures();
//        } catch (Exception e) {
//            log.warn("⚠️ 패킷 캡처 실패, 기본값으로 대체", e);
//            features = new HashMap<>();
//            features.put("dur", 0.0);
//            features.put("proto", "tcp");
//            features.put("service", "-");
//            features.put("state", "INT");
//        }

            // AI 판단
            if (aiClient.checkBlocked(features)) {
                log.warn("🚨 AI에 의해 비정상적인 비밀번호 변경 시도로 차단됨: email={}", request.getEmail());
                String message = ErrorCode.AI_BLOCKED.formatMessage("비밀번호 변경", user.getEmail());
                throw new BusinessException(ErrorCode.AI_BLOCKED, "비밀번호 변경", user.getEmail());
            }

            // 비밀번호 변경
            mailService.resetPasswordByCode(request.getEmail(), request.getCode(), request.getNewPassword());
            log.info("✅ 비밀번호 변경 성공: email={}", user.getEmail());
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));

        } catch (Exception e) {
            log.error("❌ 비밀번호 변경 처리 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "비밀번호 변경 처리 중 오류가 발생했습니다."));
        }
    }
}