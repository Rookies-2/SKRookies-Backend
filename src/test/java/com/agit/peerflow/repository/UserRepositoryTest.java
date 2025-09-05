package com.agit.peerflow.repository;

import com.agit.peerflow.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Rollback(false)
    void testRegisterUser() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "student" + timestamp + "@example.com";
        String uniqueNickname = "hong" + timestamp;  // 닉네임도 유니크하게

        User user = User.builder()
                .email(uniqueEmail)
                .password("password123")
                .username("홍길동")
                .nickname(uniqueNickname)
                .role("STUDENT")
                .status("PENDING")
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getStatus()).isEqualTo("PENDING");
    }



    /**
     * 승인 대기 사용자 조회 테스트
     */
    @Test
    @Transactional
    void testFindPendingUsers() {
        // 테스트용 사용자 등록
        User user = User.builder()
                .email("pendinguser@example.com")
                .password("1234")
                .username("대기자")
                .nickname("pending123")
                .role("STUDENT")
                .status("PENDING")
                .build();
        userRepository.save(user);

        List<User> pendingUsers = userRepository.findByStatus("PENDING");
        assertThat(pendingUsers).isNotEmpty();
        pendingUsers.forEach(u -> assertThat(u.getStatus()).isEqualTo("PENDING"));
    }

    /**
     * 사용자 승인 테스트
     */
    @Test
    @Transactional
    void testApproveUser() {
        // 테스트용 사용자 생성
        User user = User.builder()
                .email("approve@example.com")
                .password("1234")
                .username("승인대상")
                .nickname("approve123")
                .role("STUDENT")
                .status("PENDING")
                .build();
        User savedUser = userRepository.save(user);

        // 승인 처리
        savedUser.setStatus("ACTIVE");
        userRepository.save(savedUser);

        // 검증
        User approvedUser = userRepository.findById(savedUser.getUserId()).orElseThrow();
        assertThat(approvedUser.getStatus()).isEqualTo("ACTIVE");
    }

    /**
     * 사용자 거부 테스트
     */
    @Test
    @Transactional
    void testRejectUser() {
        // 테스트용 사용자 생성
        User user = User.builder()
                .email("reject@example.com")
                .password("1234")
                .username("거부대상")
                .nickname("reject123")
                .role("STUDENT")
                .status("PENDING")
                .build();
        User savedUser = userRepository.save(user);

        // 거부 처리
        savedUser.setStatus("REJECTED");
        userRepository.save(savedUser);

        // 검증
        User rejectedUser = userRepository.findById(savedUser.getUserId()).orElseThrow();
        assertThat(rejectedUser.getStatus()).isEqualTo("REJECTED");
    }

    /**
     * 존재하지 않는 사용자 조회 예외 테스트
     */
    @Test
    void testNotFoundUser() {
        // 존재하지 않는 ID로 조회 시 RuntimeException 발생
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            userRepository.findById(9999L)
                    .orElseThrow(() -> new RuntimeException("User Not Found"));
        });
    }
}
