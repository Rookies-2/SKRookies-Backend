package com.agit.peerflow.security;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.Packet;

import java.util.HashMap;
import java.util.Map;

public class FeatureExtractor {

    public static Map<String, Object> extract(Packet packet) {
        Map<String, Object> features = new HashMap<>();

        // 기본값 (누락 방지)
        features.put("dur", 0.1);
        features.put("proto", "-");
        features.put("service", "-");
        features.put("state", "INT");

        // spkts, dpkts, bytes 등
        features.put("spkts", 1);
        features.put("dpkts", 0);
        features.put("sbytes", packet.length());
        features.put("dbytes", 0);

        // IPv4 헤더
        IpV4Packet ip = packet.get(IpV4Packet.class);
        if (ip != null) {
            features.put("sttl", ip.getHeader().getTtlAsInt());
            features.put("dttl", 0); // 응답 패킷 필요 시 갱신
            features.put("proto", ip.getHeader().getProtocol().name());
        }

        // TCP 헤더
        TcpPacket tcp = packet.get(TcpPacket.class);
        if (tcp != null) {
            features.put("swin", tcp.getHeader().getWindowAsInt());
            features.put("stcpb", tcp.getHeader().getSequenceNumber());
            features.put("dwin", 0);
            features.put("tcprtt", 0.0);
            features.put("synack", 0.0);
            features.put("ackdat", 0.0);
        }

        // UDP 헤더
        UdpPacket udp = packet.get(UdpPacket.class);
        if (udp != null) {
            features.put("swin", 0);
            features.put("stcpb", 0);
        }

        // 평균 패킷 크기 (smean, dmean)
        features.put("smean", packet.length());
        features.put("dmean", 0);

        // 기타 지표 기본값 (추후 세부 계산 가능)
        features.put("rate", packet.length() / 0.1);
        features.put("sload", 0.0);
        features.put("dload", 0.0);
        features.put("sloss", 0);
        features.put("dloss", 0);
        features.put("sinpkt", 0.0);
        features.put("dinpkt", 0.0);
        features.put("sjit", 0.0);
        features.put("djit", 0.0);

        // 연결 관련 지표 기본값
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

        return features;
    }
}