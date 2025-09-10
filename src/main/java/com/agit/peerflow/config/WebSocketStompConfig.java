package com.agit.peerflow.config;

import com.agit.peerflow.security.component.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
 * @author  백두현
 * @version 1.0
 * @since   2025-09-07
 * @description: STOMP 프로토콜 지원하여 다중 채팅방, 브로드캐스트, 1:1메시징 지원하는 고수준 접근 방식의 config, Pub/Sub 모델기반
 * - STOMP 엔드포인트 등록
 * - 메시지 브로커 설정
 * - 클라이언트와 서버 간 실시간 양방향 통신 지원
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor; // 채팅 시 사용자 princple 세팅 위한 인터셉터

    // 웹소켓 클라이언트들이 서버에 접속할 수 있는 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 연결 엔드포인트 => /stomp
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS(); // 오래된 브라우저 호환을 위해서 필요.
    }

    // 메시지 브로커의 동작 방식 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /topic - 브로드캐스트용
        // /queue - 1:1 messaging
        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(myMessageBrokerTaskScheduler()); // 서버 <-> 클라이언트 간 heartbeat
        // /app - @MessageMapping 메서드로 라우팅, 클라이언트가 메시지 발행
        registry.setApplicationDestinationPrefixes("/app");
        // /user - 유저 별
        registry.setUserDestinationPrefix("/user");
    }

    @Bean
    public TaskScheduler myMessageBrokerTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("heartbeat-thread-");
        scheduler.initialize();
        return scheduler;
    }
}
