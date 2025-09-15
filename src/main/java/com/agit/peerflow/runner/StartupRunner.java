package com.agit.peerflow.runner;

import com.agit.peerflow.ai.AiClient;
import com.agit.peerflow.security.service.PacketCaptureService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final PacketCaptureService captureService;
    private final AiClient aiClient;

    public StartupRunner(PacketCaptureService captureService, AiClient aiClient) {
        this.captureService = captureService;
        this.aiClient = aiClient;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 네트워크 패킷 캡처 시작...");
        var features = captureService.captureFeatures();
        boolean blocked = aiClient.checkBlocked(features);

        if (blocked) {
            System.out.println("🚨 공격 탐지됨: " + features);
        } else {
            System.out.println("✅ 정상 트래픽: " + features);
        }
    }
}