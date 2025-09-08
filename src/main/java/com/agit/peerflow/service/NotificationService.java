package com.agit.peerflow.service;

import com.agit.peerflow.domain.Notification;
import com.agit.peerflow.domain.User;
import com.agit.peerflow.domain.enums.NotificationType;
import com.agit.peerflow.dto.notification.NotificationResponse;
import com.agit.peerflow.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-08
 * @description 알림(히스토리) 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림 생성 (다른 서비스에서 호출됨)
     */
    @Transactional
    public void createNotification(User user, String content, String relatedUrl, NotificationType notificationType) {
        Notification notification = Notification.createNotification(user, content, relatedUrl, notificationType);
        notificationRepository.save(notification);
    }

    /**
     * 특정 사용자의 모든 알림 조회
     */
    public List<NotificationResponse> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 알림을 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        if (!notification.getUser().getId().equals(userId)) {
            throw new SecurityException("자신의 알림만 읽음 처리할 수 있습니다.");
        }

        notification.markAsRead();
    }
}