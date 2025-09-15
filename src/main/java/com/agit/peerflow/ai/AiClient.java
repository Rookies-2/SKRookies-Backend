package com.agit.peerflow.ai;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class AiClient {

    private final WebClient webClient;

    public AiClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:5001").build();
    }

    /**
     * AI 서버로 특징 데이터 전달 → 공격 여부 확인
     */
    public boolean checkBlocked(Map<String, Object> features) {
//        return webClient.post()
//                .uri("/detect")
//                .bodyValue(features)
//                .retrieve()
//                .bodyToMono(Map.class)
//                .map(response -> Boolean.TRUE.equals(response.get("blocked")))
//                .block();
        // AI 서버 호출 없이 항상 로그인 허용
        return false;
    }
}
