package com.agit.peerflow.config;

import com.agit.peerflow.entity.User;
import com.agit.peerflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    //ADMIN계정 생성
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByEmail("admin@example.com")) {
                User admin = User.builder()
                        .email("admin@example.com")
                        .username("관리자")
                        .password("{noop}admin123") // Spring Security 기본 인증 (NoOpPasswordEncoder)
                        .nickname("admin")
                        .role("ADMIN")
                        .status("ACTIVE")
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Admin account created: admin@example.com / admin123");
            }
        };
    }
}
