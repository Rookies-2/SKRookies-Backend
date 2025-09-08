package com.agit.peerflow.util;

import com.agit.peerflow.domain.User;
import com.agit.peerflow.domain.enums.Role;
import com.agit.peerflow.domain.enums.UserStatus; // 👈 이 줄을 추가하세요!
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-08
 * @description 개발 환경에서 테스트용 유저 데이터를 미리 생성하는 클래스
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 테스트용 강사 계정이 없으면 생성
        userRepository.findByUsername("teacher").orElseGet(() ->
                userRepository.save(User.builder()
                        .username("teacher")
                        .password(passwordEncoder.encode("password"))
                        .nickname("김강사")
                        .role(Role.TEACHER)
                        .status(UserStatus.ACTIVE) // 이제 UserStatus를 찾을 수 있습니다.
                        .build())
        );

        // 테스트용 학생 계정이 없으면 생성
        userRepository.findByUsername("student").orElseGet(() ->
                userRepository.save(User.builder()
                        .username("student")
                        .password(passwordEncoder.encode("password"))
                        .nickname("이학생")
                        .role(Role.STUDENT)
                        .status(UserStatus.ACTIVE) // 이제 UserStatus를 찾을 수 있습니다.
                        .build())
        );

        System.out.println("---- Test users created ----");
    }
}