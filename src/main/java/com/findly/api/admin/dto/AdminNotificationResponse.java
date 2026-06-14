package com.findly.api.admin.dto;

import com.findly.api.common.enums.NotificationType;
import com.findly.api.notifications.entity.Notification;

import java.time.Instant;
import java.util.UUID;

public record AdminNotificationResponse(
        UUID id,
        UUID userId,
        String userName,
        String userEmail,
        NotificationType type,
        String title,
        String message,
        boolean read,
        UUID reportId,
        UUID claimId,
        Instant createdAt,
        Instant updatedAt
) {

    public static AdminNotificationResponse fromNotification(Notification notification) {
        return new AdminNotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                notification.getUser().getFullName(),
                notification.getUser().getEmail(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getReport() == null ? null : notification.getReport().getId(),
                notification.getClaim() == null ? null : notification.getClaim().getId(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}