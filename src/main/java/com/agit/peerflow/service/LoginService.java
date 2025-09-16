package com.agit.peerflow.service;

import com.agit.peerflow.ai.AiClient;
import com.agit.peerflow.domain.entity.LoginAttemptLog;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.dto.auth.LoginRequestDto;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.LoginAttemptLogRepository;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.security.component.JwtTokenProvider;
import com.agit.peerflow.security.service.PacketCaptureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.agit.peerflow.security.component.JwtTokenProvider;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AiClient aiClient;
    private final PacketCaptureService packetCaptureService;
    private final LoginAttemptLogRepository loginAttemptLogRepository;
    private final ObjectMapper objectMapper;
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    public ResponseEntity<?> login(LoginRequestDto requestDto, HttpServletRequest httpRequest) {
        try {
            // 사용자 조회
            User user = userRepository.findByEmail(requestDto.getEmail())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "User", "email", requestDto.getEmail()));
            // 1. 오늘 로그인 시도 횟수 확인
            long todayAttempts = loginAttemptLogRepository.countTodayByUserEmail(
                    requestDto.getEmail(),
                    LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN),
                    LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX)
            );
            // 2. 최대 시도 횟수 초과 여부 검증
            if (todayAttempts >= MAX_LOGIN_ATTEMPTS) {
                log.warn("❌ 로그인 시도 횟수 초과: email={}", requestDto.getEmail());
                throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "로그인 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.");
            }
            //실제 네트워크 트래픽용
            // 패킷 특성 추출
            Map<String, Object> features;
            try {
                features = packetCaptureService.captureFeatures();
                log.info("📡 캡처된 패킷 특성: {}", features);
            } catch (Exception e) {
                log.error("⚠️ 패킷 캡처 실패. 기본값으로 대체합니다.", e);
                features = new HashMap<>();
                features.put("dur", 0.0);
                features.put("proto", "tcp");
                features.put("service", "-");
                features.put("state", "INT");
                // 최소 필드만 세팅 (AI 모델이 null 받지 않도록)
            }
// AI 판단 시연용 코드
//        Map<String, Object> features = new HashMap<>();
//        //로그인차단예시
//            features.put("dur", 0.921987);
//            features.put("proto", "ospf");
//            features.put("service", "-");
//            features.put("state", "INT");
//            features.put("spkts", 20);
//            features.put("dpkts", 0);
//            features.put("sbytes", 1280);
//            features.put("dbytes", 0);
//            features.put("rate", 20.607666);
//            features.put("sttl", 254);
//            features.put("dttl", 0);
//            features.put("sload", 10551.125);
//            features.put("dload", 0);
//            features.put("sloss", 0);
//            features.put("dloss", 0);
//            features.put("sinpkt", 48.525633);
//            features.put("dinpkt", 0);
//            features.put("sjit", 52.253805);
//            features.put("djit", 0);
//            features.put("swin", 0);
//            features.put("dwin", 0);
//            features.put("tcprtt", 0);
//            features.put("synack", 0);
//            features.put("ackdat", 0);
//            features.put("smean", 64);
//            features.put("dmean", 0);
//            features.put("trans_depth", 0);
//            features.put("response_body_len", 0);
//            features.put("ct_srv_src", 1);
//            features.put("ct_state_ttl", 2);
//            features.put("ct_dst_ltm", 1);
//            features.put("ct_src_dport_ltm", 1);
//            features.put("ct_dst_sport_ltm", 1);
//            features.put("ct_dst_src_ltm", 2);
//            features.put("is_ftp_login", 0);
//            features.put("ct_ftp_cmd", 0);
//            features.put("ct_flw_http_mthd", 0);
//            features.put("ct_src_ltm", 1);
//            features.put("ct_srv_dst", 1);
//            features.put("is_sm_ips_ports", 0);

            boolean isBlockedByAi = aiClient.checkBlocked(features);
            boolean isPasswordCorrect = passwordEncoder.matches(requestDto.getPassword(), user.getPassword());

            // 로그인 실패 시 로그 저장 후 예외 발생
            if (isBlockedByAi || !isPasswordCorrect) {
                int currentAttemptCount = (int) todayAttempts + 1;
                // 실패 로그를 저장합니다.
                saveLoginAttemptLog(httpRequest, user, features, isBlockedByAi, isPasswordCorrect, currentAttemptCount);

                // 로그 저장 후 예외를 던집니다.
                if (isBlockedByAi) {
                    log.warn("🚫 AI에 의해 로그인 차단됨: email={}", requestDto.getEmail());
                    throw new BusinessException(ErrorCode.AI_BLOCKED, "로그인", user.getEmail());
                } else {
                    log.warn("❌ 비밀번호 불일치: email={}", requestDto.getEmail());
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호가 일치하지 않습니다.");
                }
            }
            // 사용자 승인 단계 체크 (로그인 성공으로 간주하지 않으므로 실패 로그를 남깁니다)
            if (user.getStatus() != UserStatus.ACTIVE) {
                int currentAttemptCount = (int) todayAttempts + 1;
                saveLoginAttemptLog(httpRequest, user, features, false, false, currentAttemptCount);
                log.warn("⚠️ 사용자 로그인 차단: 승인 대기 상태. email={}", requestDto.getEmail());
                throw new BusinessException(ErrorCode.ACCESS_DENIED, "사용자 승인 단계입니다.");
            }
            // ✅ 모든 검증 절차를 통과한 후, 마지막 로그인 시간 업데이트
            user.updateLastLoginTime();
            userRepository.save(user); // 변경된 엔티티 저장

            // JWT 생성
            Map<String, Object> claims = Map.of("role", user.getRole().name());
            String token = jwtTokenProvider.createToken(user.getEmail(), claims);

            log.info("✅ 로그인 성공: email={}", requestDto.getEmail());
            return ResponseEntity.ok(Map.of("blocked", false,"accessToken", token));

        } catch (Exception e) {
            log.error("❌ 로그인 처리 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "로그인 처리 중 오류가 발생했습니다."));
        }
    }
    @Transactional
    private void saveLoginAttemptLog(HttpServletRequest httpRequest, User user,
                                     Map<String, Object> features, boolean isAiBlocked, boolean isSuccess, int attemptCount) {
        try {
            LoginAttemptLog logEntry = LoginAttemptLog.builder()
                    .user(user)
                    .email(user.getEmail())
                    .ip(httpRequest.getRemoteAddr())
                    .device(httpRequest.getHeader("User-Agent"))
                    .aiBlocked(isAiBlocked)
                    .success(isSuccess)
                    .attemptCount(attemptCount)
                    .features(objectMapper.writeValueAsString(features))
                    .build();
            loginAttemptLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("⚠️ 로그인 시도 로그 저장 중 예외 발생: email={}", user.getEmail(), e);
        }
    }
}