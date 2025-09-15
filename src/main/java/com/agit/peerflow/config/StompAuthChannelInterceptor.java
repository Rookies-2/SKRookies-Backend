package com.agit.peerflow.config;

import com.agit.peerflow.security.component.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/*
 * @author  백두현
 * @version 1.0
 * @since   2025-09-11
 * @description: 메시지 송수신 시 WebSocket에서 토큰을 가질 수 있도록 처리함
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // STOMP 연결 요청(CONNECT)일 때만 JWT 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 헤더에서 Authorization 토큰 추출
            String authToken = accessor.getFirstNativeHeader("Authorization");
            System.out.println("StompAuthChannelInterceptor authToken: " + authToken);

            if (authToken != null && authToken.startsWith("Bearer ")) {
                String jwt = authToken.substring(7);
                // 토큰이 유효하다면
                if (jwtTokenProvider.validateToken(jwt)) {
                    // 토큰에서 사용자 이름(email) 추출
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);
                    // 사용자 정보 로드
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Spring Security 인증 토큰 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    // 메시지 헤더에 인증 정보 설정 -> 이후 컨트롤러에서 Principal로 주입받음
                    accessor.setUser(authentication);
                }
            }
        }
        return message;
    }
}
