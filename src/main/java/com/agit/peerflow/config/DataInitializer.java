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
                User admin = User.createUser(
                        "관리자",
                        "{noop}admin123",
                        "admin",
                        "example@naver.com",
                        UserRole.ADMIN,
                        UserStatus.ACTIVE
                );
                userRepository.save(admin);
                System.out.println("✅ Admin account created: admin@example.com / admin123");
            }
        };
    }
}
