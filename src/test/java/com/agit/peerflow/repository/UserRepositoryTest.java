package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * 사용자 등록 테스트
     */
    @Test
    @Transactional
    @Rollback(false)
    void testRegisterUser() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "student" + timestamp + "@example.com";
        String uniqueNickname = "hong" + timestamp;

        User user = User.builder()
                .email(uniqueEmail)
                .password("password123")
                .username("홍길동")
                .nickname(uniqueNickname)
                .role(UserRole.STUDENT)
                .status(UserStatus.PENDING)
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.PENDING);
    }

    /**
     * 승인 대기 사용자 조회 테스트 (페이징 포함)
     */
    @Test
    @Transactional
    void testFindPendingUsersPaged() {
        User user = User.builder()
                .email("pendinguser@example.com")
                .password("1234")
                .username("대기자")
                .nickname("pending123")
                .role(UserRole.STUDENT)
                .status(UserStatus.PENDING)
                .build();
        userRepository.save(user);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> pendingUsers = userRepository.findByStatus(UserStatus.PENDING, pageable);

        assertThat(pendingUsers.getContent()).isNotEmpty();
        pendingUsers.forEach(u -> assertThat(u.getStatus()).isEqualTo(UserStatus.PENDING));
    }

    /**
     * 사용자 승인 테스트
     */
    @Test
    @Transactional
    void testApproveUser() {
        User user = User.builder()
                .email("approve@example.com")
                .password("1234")
                .username("승인대상")
                .nickname("approve123")
                .role(UserRole.STUDENT)
                .status(UserStatus.PENDING)
                .build();
        User savedUser = userRepository.save(user);

        savedUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(savedUser);

        User approvedUser = userRepository.findById(savedUser.getUserId()).orElseThrow();
        assertThat(approvedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    /**
     * 사용자 거부 테스트
     */
    @Test
    @Transactional
    void testRejectUser() {
        User user = User.builder()
                .email("reject@example.com")
                .password("1234")
                .username("거부대상")
                .nickname("reject123")
                .role(UserRole.STUDENT)
                .status(UserStatus.PENDING)
                .build();
        User savedUser = userRepository.save(user);

        savedUser.setStatus(UserStatus.REJECTED);
        userRepository.save(savedUser);

        User rejectedUser = userRepository.findById(savedUser.getUserId()).orElseThrow();
        assertThat(rejectedUser.getStatus()).isEqualTo(UserStatus.REJECTED);
    }

    /**
     * 존재하지 않는 사용자 조회 예외 테스트
     */
    @Test
    void testNotFoundUser() {
        assertThrows(BusinessException.class, () -> {
            userService.getUserById(9999L);
        });
    }
}
