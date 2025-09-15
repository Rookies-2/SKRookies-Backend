package com.agit.peerflow.scheduler;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.service.AdminService;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InactiveUserDeactivationScheduler {

    private final UserRepository userRepository;
    private final AdminService adminService;

    // 매일 새벽 4시 30분에 실행
    @Scheduled(cron = "0 30 4 * * ?")
    public void deactivateInactiveUsers() {
        log.info("🗓️ 비활성 사용자 계정 정리 스케줄러 시작");
        
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        
        // 1년 이상 로그인하지 않은 ACTIVE 상태의 사용자 목록 조회
        List<User> inactiveUsers = userRepository.findByLastLoggedInAtBeforeAndStatus(oneYearAgo, UserStatus.ACTIVE);
        
        if (inactiveUsers.isEmpty()) {
            log.info("✅ 비활성화할 사용자 계정이 없습니다.");
            return;
        }
        
        log.info("⏰ 총 {}개의 계정을 비활성화합니다.", inactiveUsers.size());
        
        for (User user : inactiveUsers) {
            try {
                // AdminService를 통해 비활성화 로직 실행
                adminService.deactivateUser(user.getId());
                log.info("➡️ 사용자 계정 비활성화 완료: ID {}", user.getId());
            } catch (Exception e) {
                log.error("❌ 사용자 계정 비활성화 중 오류 발생: ID {}", user.getId(), e);
            }
        }
        
        log.info("🎉 비활성 사용자 계정 정리 스케줄러 종료");
    }
}