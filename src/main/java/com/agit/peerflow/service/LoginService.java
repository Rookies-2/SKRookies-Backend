package com.agit.peerflow.service;

import com.agit.peerflow.ai.AiClient;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.auth.LoginRequestDto;
import com.agit.peerflow.dto.user.UserDTO;
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

    public ResponseEntity<?> login(LoginRequestDto requestDto, HttpServletRequest httpRequest) {
//        User user = userRepository.findByEmail(requestDto.getEmail())
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//
//        // 1️⃣ 비밀번호 검증
//        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "비밀번호가 일치하지 않습니다."));
//        }
//
//// 2️⃣ AI 판단
//        Map<String, Object> features = packetCaptureService.captureFeatures(packet);
////        // 2️⃣ AI 판단
////        Map<String, Object> features = new HashMap<>();
//        //로그인성공예시
////            features.put("dur", 0.000011);
////            features.put("proto", "udp");
////            features.put("service", "-");
////            features.put("state", "INT");
////            features.put("spkts", 2);
////            features.put("dpkts", 0);
////            features.put("sbytes", 496);
////            features.put("dbytes", 0);
////            features.put("rate", 90909.0902);
////            features.put("sttl", 254);
////            features.put("dttl", 0);
////            features.put("sload", 180363632);
////            features.put("dload", 0);
////            features.put("sloss", 0);
////            features.put("dloss", 0);
////            features.put("sinpkt", 0.011);
////            features.put("dinpkt", 0);
////            features.put("sjit", 0);
////            features.put("djit", 0);
////            features.put("swin", 0);
////            features.put("dwin", 0);
////            features.put("tcprtt", 0);
////            features.put("synack", 0);
////            features.put("ackdat", 0);
////            features.put("smean", 248);
////            features.put("dmean", 0);
////            features.put("trans_depth", 0);
////            features.put("response_body_len", 0);
////            features.put("ct_srv_src", 2);
////            features.put("ct_state_ttl", 2);
////            features.put("ct_dst_ltm", 1);
////            features.put("ct_src_dport_ltm", 1);
////            features.put("ct_dst_sport_ltm", 1);
////            features.put("ct_dst_src_ltm", 2);
////            features.put("is_ftp_login", 0);
////            features.put("ct_ftp_cmd", 0);
////            features.put("ct_flw_http_mthd", 0);
////            features.put("ct_src_ltm", 1);
////            features.put("ct_srv_dst", 2);
////            features.put("is_sm_ips_ports", 0);
//        //로그인차단예시
////            features.put("dur", 0.921987);
////            features.put("proto", "ospf");
////            features.put("service", "-");
////            features.put("state", "INT");
////            features.put("spkts", 20);
////            features.put("dpkts", 0);
////            features.put("sbytes", 1280);
////            features.put("dbytes", 0);
////            features.put("rate", 20.607666);
////            features.put("sttl", 254);
////            features.put("dttl", 0);
////            features.put("sload", 10551.125);
////            features.put("dload", 0);
////            features.put("sloss", 0);
////            features.put("dloss", 0);
////            features.put("sinpkt", 48.525633);
////            features.put("dinpkt", 0);
////            features.put("sjit", 52.253805);
////            features.put("djit", 0);
////            features.put("swin", 0);
////            features.put("dwin", 0);
////            features.put("tcprtt", 0);
////            features.put("synack", 0);
////            features.put("ackdat", 0);
////            features.put("smean", 64);
////            features.put("dmean", 0);
////            features.put("trans_depth", 0);
////            features.put("response_body_len", 0);
////            features.put("ct_srv_src", 1);
////            features.put("ct_state_ttl", 2);
////            features.put("ct_dst_ltm", 1);
////            features.put("ct_src_dport_ltm", 1);
////            features.put("ct_dst_sport_ltm", 1);
////            features.put("ct_dst_src_ltm", 2);
////            features.put("is_ftp_login", 0);
////            features.put("ct_ftp_cmd", 0);
////            features.put("ct_flw_http_mthd", 0);
////            features.put("ct_src_ltm", 1);
////            features.put("ct_srv_dst", 1);
////            features.put("is_sm_ips_ports", 0);
//        boolean blocked = aiClient.checkBlocked(features);
//        if (blocked) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("blocked", true, "message", "AI 판단에 의해 로그인 차단됨"));
//        }
//
//        // 3️⃣ JWT 생성
//        Map<String, Object> claims = Map.of("role", user.getRole().name());
//        String token = jwtTokenProvider.createToken(user.getEmail(), claims);
//
//        return ResponseEntity.ok(Map.of(
//                "blocked", false,
//                "accessToken", token
//        ));
//    }
        try {
            // 1️⃣ 사용자 조회
            User user = userRepository.findByEmail(requestDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 2️⃣ 비밀번호 검증
            if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
                log.warn("❌ 비밀번호 불일치: email={}", requestDto.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "비밀번호가 일치하지 않습니다."));
            }

            // 3️⃣ 패킷 특성 추출
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

            // 4️⃣ AI 판단
            boolean blocked = aiClient.checkBlocked(features);
            if (blocked) {
                log.warn("🚫 로그인 차단됨: email={}, features={}", requestDto.getEmail(), features);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("blocked", true, "message", "AI 판단에 의해 로그인 차단됨"));
            }

            // 5️⃣ JWT 생성
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