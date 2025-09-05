package com.agit.peerflow.repository;

import com.agit.peerflow.entity.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * 사용자 등록 테스트
     */
    @Test
    @Transactional
    @Rollback(false) // DB에 실제 저장
    void testRegisterUser() {
        User user = User.builder()
                .email("student1@example.com")
                .password("password123")
                .username("홍길동")
                .nickname("hong123")
                .role("STUDENT")
                .status("PENDING") // 가입 시 기본 승인 대기
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getStatus()).isEqualTo("PENDING");
    }

    /**
     * 승인 대기 사용자 조회 테스트
     */
    @Test
    void testFindPendingUsers() {
        List<User> pendingUsers = userRepository.findByStatus("PENDING");
        assertThat(pendingUsers).isNotEmpty(); // 하나 이상 존재해야 함
        pendingUsers.forEach(u -> assertThat(u.getStatus()).isEqualTo("PENDING"));
    }

    /**
     * 사용자 승인 테스트
     */
    @Test
    @Transactional
    @Rollback(false)
    void testApproveUser() {
        // 예시: 이메일로 사용자 조회
        Optional<User> userOpt = userRepository.findById(1L);
        userOpt.ifPresent(u -> {
            u.setStatus("ACTIVE");
            userRepository.save(u);
        });

        User approvedUser = userRepository.findById(1L).orElseThrow();
        assertThat(approvedUser.getStatus()).isEqualTo("ACTIVE");
    }

    /**
     * 사용자 거부 테스트
     */
    @Test
    @Transactional
    @Rollback(false)
    void testRejectUser() {
        Optional<User> userOpt = userRepository.findById(2L);
        userOpt.ifPresent(u -> {
            u.setStatus("REJECTED");
            userRepository.save(u);
        });

        User rejectedUser = userRepository.findById(2L).orElseThrow();
        assertThat(rejectedUser.getStatus()).isEqualTo("REJECTED");
    }

    /**
     * 존재하지 않는 사용자 조회 예외 테스트
     */
    @Test
    @Disabled
    void testNotFoundUser() {
        User notFoundUser = userRepository.findById(999L)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }
}
