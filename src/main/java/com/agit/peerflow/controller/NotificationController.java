// controller/NotificationController.java

package com.agit.peerflow.controller;

// import com.agit.peerflow.domain.User; // 임시 테스트용 User 객체는 더 이상 필요 없음
import com.agit.peerflow.dto.notification.NotificationResponse;
import com.agit.peerflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 현재 로그인한 사용자의 모든 알림 조회 (임시 테스트용)
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(@RequestParam Long userId) {
        // userId를 서비스에 직접 전달
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 특정 알림을 읽음으로 표시 (임시 테스트용)
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }
}