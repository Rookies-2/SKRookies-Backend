package com.agit.peerflow.security.config;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.security.handler.CustomAccessDeniedHandler;
import com.agit.peerflow.security.jwt.JwtAuthenticationFilter;
import com.agit.peerflow.security.service.UserDetailsServiceImpl;
import com.agit.peerflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author 백두현
 * @version   1.0.0
 * @since     2025-09-08
 * @description
 * 세션관리방식: JWT 인증에 맞게 비활성화
 * formLogin/logout: JWT 기반은 프론트 단에서 처리.
 * TODO 01. 인증 실패 시 401 Unauthorized 에러를 표출하고 프론트가 로그인 처리를 담당하게 하기 (logout, 인증 실패)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsServiceImpl userDetailsService,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable) // basic authentication filter 비활성화
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
                // 세션을 사용하지 않는 Stateless 방식으로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 백두현: Spring Security의 필터체인은 순차적으로 검사하므로 아래 순서를 지켜야 함.
                        // 1. 인증 없이 접근 가능한 경로
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(
                                "/api/auth/password/reset",
                                "/api/auth/password/verify",
                                "/api/auth/password/update"
                        ).permitAll()
                        .requestMatchers("/api/users/signup").permitAll()
                        .requestMatchers("/stomp/**").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // 2. 나머지 /api/users/**, /api/chatrooms/**는 인증 필요
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/chatrooms/**").authenticated()
                        // 3. 관리자 전용
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        // 4. 김현근 : 스웨거 문서 접근허용
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // 5. 그 외 모든 요청 인증 필요
                        .anyRequest().authenticated()


                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // spring security6 부턴 람다 스타일로 authenticationManager 설정
                .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder))
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(customAccessDeniedHandler)
                ); // 권한 예외 처리기 등록

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // Spring security6 부터는 DaoAuthenticationProvider#setUserDetailsService(...) 메서드가 Deprecated되어 생성자에서 바로 주입받는 방식으로 바뀜.
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

}
