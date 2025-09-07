package com.agit.peerflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
        ////관리자모드 주석처리
//            .authorizeHttpRequests(auth -> auth
//            // 관리자만 전체 목록 조회 가능
//            .requestMatchers("/api/users").hasRole("ADMIN")
//            // 나머지는 모두 허용
//            .anyRequest().permitAll()
//                )
//                .formLogin(form -> form.permitAll()); // 간단한 로그인 폼 허용
        ////관리자모드 주석처리
        /// 테스트용 모든계정상관없이 요청 허용
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // 모든 요청 허용
        return http.build();
    }
}
