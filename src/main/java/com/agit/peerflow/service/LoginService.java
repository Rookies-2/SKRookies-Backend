package com.agit.peerflow.service;

import com.agit.peerflow.ai.AiClient;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.dto.auth.LoginRequestDto;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.security.component.JwtTokenProvider;
import com.agit.peerflow.security.service.PacketCaptureService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // ================== 공통 ResponseEntity 헬퍼 ==================
    private ResponseEntity<Map<String, Object>> buildResponse(boolean blocked, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of("blocked", blocked, "message", message));
    }

    private ResponseEntity<Map<String, Object>> buildAiBlockedResponse(String action, String email) {
        String message = ErrorCode.AI_BLOCKED.formatMessage(action, email);
        return ResponseEntity.status(ErrorCode.AI_BLOCKED.getHttpStatus())
                .body(Map.of("blocked", true, "message", message));
    }

    public ResponseEntity<?> login(LoginRequestDto requestDto, HttpServletRequest httpRequest) {
        try {
            // 사용자 조회
            User user = userRepository.findByEmail(requestDto.getEmail())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "User", "email", requestDto.getEmail()));

            // 비밀번호 검증
            if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
                log.warn("❌ 비밀번호 불일치: email={}", requestDto.getEmail());
                return buildResponse(false, "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);

            }
            // 사용자 승인 단계 체크
            if (user.getStatus() != UserStatus.ACTIVE) {
                log.warn("⚠️ 사용자 로그인 차단: 승인 대기 상태. email={}", requestDto.getEmail());
                return buildResponse(false, "사용자 승인 단계입니다.", HttpStatus.FORBIDDEN);
            }
// AI 판단
//        Map<String, Object> features = new HashMap<>();
//        //로그인성공예시
//            features.put("dur", 0.000011);
//            features.put("proto", "udp");
//            features.put("service", "-");
//            features.put("state", "INT");
//            features.put("spkts", 2);
//            features.put("dpkts", 0);
//            features.put("sbytes", 496);
//            features.put("dbytes", 0);
//            features.put("rate", 90909.0902);
//            features.put("sttl", 254);
//            features.put("dttl", 0);
//            features.put("sload", 180363632);
//            features.put("dload", 0);
//            features.put("sloss", 0);
//            features.put("dloss", 0);
//            features.put("sinpkt", 0.011);
//            features.put("dinpkt", 0);
//            features.put("sjit", 0);
//            features.put("djit", 0);
//            features.put("swin", 0);
//            features.put("dwin", 0);
//            features.put("tcprtt", 0);
//            features.put("synack", 0);
//            features.put("ackdat", 0);
//            features.put("smean", 248);
//            features.put("dmean", 0);
//            features.put("trans_depth", 0);
//            features.put("response_body_len", 0);
//            features.put("ct_srv_src", 2);
//            features.put("ct_state_ttl", 2);
//            features.put("ct_dst_ltm", 1);
//            features.put("ct_src_dport_ltm", 1);
//            features.put("ct_dst_sport_ltm", 1);
//            features.put("ct_dst_src_ltm", 2);
//            features.put("is_ftp_login", 0);
//            features.put("ct_ftp_cmd", 0);
//            features.put("ct_flw_http_mthd", 0);
//            features.put("ct_src_ltm", 1);
//            features.put("ct_srv_dst", 2);
//            features.put("is_sm_ips_ports", 0);
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

            // AI 판단
            if (aiClient.checkBlocked(features)) {
                log.warn("🚫 로그인 차단됨: email={}, features={}", requestDto.getEmail(), features);
                return buildAiBlockedResponse("로그인", user.getEmail());
            }
            // ✅ 모든 검증 절차를 통과한 후, 마지막 로그인 시간 업데이트
            user.updateLastLoginTime();
            userRepository.save(user); // 변경된 엔티티 저장

            // JWT 생성
            Map<String, Object> claims = Map.of("role", user.getRole().name());
            String token = jwtTokenProvider.createToken(user.getEmail(), claims);

            log.info("✅ 로그인 성공: email={}", requestDto.getEmail());
            return ResponseEntity.ok(Map.of(
                    "blocked", false,
                    "accessToken", token
            ));

        } catch (Exception e) {
            log.error("❌ 로그인 처리 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "로그인 처리 중 오류가 발생했습니다."));
        }
    }
}