package com.agit.peerflow.security.service;

import com.agit.peerflow.ai.AiClient;
import com.agit.peerflow.security.FeatureExtractor;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PacketCaptureService {    private static final int SNAPLEN = 65536; // 캡처 최대 바이트
    private static final int TIMEOUT = 10;    // ms
    private static final int PACKET_COUNT = 10; // 캡처할 패킷 개수

    public Map<String, Object> captureFeatures() throws PcapNativeException, NotOpenException {
        // 1. 네트워크 디바이스 선택
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
        if (allDevs == null || allDevs.isEmpty()) {
            throw new RuntimeException("네트워크 디바이스를 찾을 수 없습니다.");
        }
        PcapNetworkInterface nif = allDevs.get(0); // 첫 번째 디바이스 사용 (필요시 변경)

        // 2. 핸들 열기
        PcapHandle handle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, TIMEOUT);

        Map<String, Object> features = new HashMap<>();
        int spkts = 0, dpkts = 0, sbytes = 0, dbytes = 0;

        // 3. 패킷 캡처
        for (int i = 0; i < PACKET_COUNT; i++) {
            Packet packet = handle.getNextPacket();
            if (packet != null) {
                spkts++;
                sbytes += packet.length();
            }
        }

        // 4. 예시 Feature 값 매핑 (실제 계산 로직 보완 필요)
        features.put("dur", 0.000011);
        features.put("proto", "udp");
        features.put("service", "-");
        features.put("state", "INT");
        features.put("spkts", spkts);
        features.put("dpkts", dpkts);
        features.put("sbytes", sbytes);
        features.put("dbytes", dbytes);
        features.put("rate", spkts > 0 ? (sbytes / (double) spkts) : 0);
        features.put("sttl", 64);
        features.put("dttl", 0);
        features.put("sload", sbytes * 100);
        features.put("dload", 0);
        features.put("sloss", 0);
        features.put("dloss", 0);
        features.put("sinpkt", 0.011);
        features.put("dinpkt", 0);
        features.put("sjit", 0);
        features.put("djit", 0);
        features.put("swin", 0);
        features.put("dwin", 0);
        features.put("tcprtt", 0);
        features.put("synack", 0);
        features.put("ackdat", 0);
        features.put("smean", spkts > 0 ? (sbytes / spkts) : 0);
        features.put("dmean", 0);
        features.put("trans_depth", 0);
        features.put("response_body_len", 0);
        features.put("ct_srv_src", 1);
        features.put("ct_state_ttl", 1);
        features.put("ct_dst_ltm", 1);
        features.put("ct_src_dport_ltm", 1);
        features.put("ct_dst_sport_ltm", 1);
        features.put("ct_dst_src_ltm", 1);
        features.put("is_ftp_login", 0);
        features.put("ct_ftp_cmd", 0);
        features.put("ct_flw_http_mthd", 0);
        features.put("ct_src_ltm", 1);
        features.put("ct_srv_dst", 1);
        features.put("is_sm_ips_ports", 0);

        handle.close();
        return features;
    }
}