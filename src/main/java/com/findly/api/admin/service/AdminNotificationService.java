package com.findly.api.admin.service;

import com.findly.api.admin.dto.AdminNotificationResponse;
import com.findly.api.common.enums.NotificationType;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.notifications.entity.Notification;
import com.findly.api.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PageResponse<AdminNotificationResponse> getNotifications(
            UUID userId,
            NotificationType type,
            Boolean read,
            String keyword,
            Integer page,
            Integer size
    ) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        List<AdminNotificationResponse> filteredNotifications = notificationRepository
                .findByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .filter(notification -> matchesUserId(notification, userId))
                .filter(notification -> matchesType(notification, type))
                .filter(notification -> matchesRead(notification, read))
                .filter(notification -> matchesKeyword(notification, keyword))
                .map(AdminNotificationResponse::fromNotification)
                .toList();

        int start = Math.min((int) pageable.getOffset(), filteredNotifications.size());
        int end = Math.min(start + pageable.getPageSize(), filteredNotifications.size());

        Page<AdminNotificationResponse> notificationsPage = new PageImpl<>(
                filteredNotifications.subList(start, end),
                pageable,
                filteredNotifications.size()
        );

        return PageResponse.fromPage(notificationsPage);
    }

    @Transactional(readOnly = true)
    public AdminNotificationResponse getNotificationById(UUID notificationId) {
        Notification notification = notificationRepository.findByIdAndDeletedFalse(notificationId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Notification not found"));

        return AdminNotificationResponse.fromNotification(notification);
    }

    @Transactional
    public void deleteNotification(UUID notificationId) {
        Notification notification = notificationRepository.findByIdAndDeletedFalse(notificationId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Notification not found"));

        notification.markDeleted();
        notificationRepository.save(notification);
    }

    private boolean matchesUserId(Notification notification, UUID userId) {
        if (userId == null) {
            return true;
        }

        return notification.getUser() != null
                && notification.getUser().getId().equals(userId);
    }

    private boolean matchesType(Notification notification, NotificationType type) {
        if (type == null) {
            return true;
        }

        return notification.getType() == type;
    }

    private boolean matchesRead(Notification notification, Boolean read) {
        if (read == null) {
            return true;
        }

        return notification.isRead() == read;
    }

    private boolean matchesKeyword(Notification notification, String keyword) {
        String cleanedKeyword = cleanNullable(keyword);

        if (cleanedKeyword == null) {
            return true;
        }

        String pattern = cleanedKeyword.toLowerCase();

        return contains(notification.getTitle(), pattern)
                || contains(notification.getMessage(), pattern)
                || contains(notification.getUser() == null ? null : notification.getUser().getFullName(), pattern)
                || contains(notification.getUser() == null ? null : notification.getUser().getEmail(), pattern);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }
}