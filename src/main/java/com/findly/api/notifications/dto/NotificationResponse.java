package com.findly.api.notifications.dto;

import com.findly.api.common.enums.NotificationType;
import com.findly.api.notifications.entity.Notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        String title,
        String message,
        boolean read,
        UUID reportId,
        UUID claimId,
        Instant createdAt
) {

    public static NotificationResponse fromNotification(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getReport() == null ? null : notification.getReport().getId(),
                notification.getClaim() == null ? null : notification.getClaim().getId(),
                notification.getCreatedAt()
        );
    }
}
