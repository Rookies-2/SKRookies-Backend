package com.agit.peerflow.config;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByEmail("admin@example.com")) {
                User admin = User.builder()
                        .email("admin@example.com")
                        .username("관리자")
                        .password("{noop}admin123") // NoOpPasswordEncoder
                        .nickname("admin")
                        .role(UserRole.ADMIN)       // Enum 사용
                        .status(UserStatus.ACTIVE)  // Enum 사용
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Admin account created: admin@example.com / admin123");
            }
        };
    }
}
