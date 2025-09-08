package com.agit.peerflow.dto.notification;

import com.agit.peerflow.domain.Notification;
import com.agit.peerflow.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-08
 * @description 알림 정보 응답을 위한 DTO
 */
@Getter
@Builder
public class NotificationResponse {
    private final Long id;
    private final String content;
    private final String relatedUrl;
    private final boolean isRead;
    private final NotificationType notificationType;
    private final LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .relatedUrl(notification.getRelatedUrl())
                .isRead(notification.isRead())
                .notificationType(notification.getNotificationType())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}