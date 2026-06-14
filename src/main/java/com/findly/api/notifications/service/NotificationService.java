package com.findly.api.notifications.service;

import com.findly.api.claims.entity.Claim;
import com.findly.api.common.enums.NotificationType;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.notifications.dto.NotificationResponse;
import com.findly.api.notifications.entity.Notification;
import com.findly.api.notifications.repository.NotificationRepository;
import com.findly.api.reports.entity.Report;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void notifyUser(
            User user,
            NotificationType type,
            String title,
            String message,
            Report report,
            Claim claim
    ) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReport(report);
        notification.setClaim(claim);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(
            Authentication authentication,
            Integer page,
            Integer size
    ) {
        User currentUser = getCurrentUser(authentication);

        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : Math.min(size, 100);

        return PageResponse.fromPage(
                notificationRepository
                        .findByUserIdAndDeletedFalseOrderByCreatedAtDesc(
                                currentUser.getId(),
                                PageRequest.of(
                                        safePage - 1,
                                        safeSize,
                                        Sort.by(Sort.Direction.DESC, "createdAt")
                                )
                        )
                        .map(NotificationResponse::fromNotification)
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getUnreadCount(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        long count = notificationRepository.countByUserIdAndReadFalseAndDeletedFalse(currentUser.getId());

        return Map.of("unreadCount", count);
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        Notification notification = notificationRepository.findById(notificationId)
                .filter(found -> !found.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Notification not found"));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN, "You are not allowed to access this notification");
        }

        notification.setRead(true);

        return NotificationResponse.fromNotification(notificationRepository.save(notification));
    }

    @Transactional
    public Map<String, Integer> markAllAsRead(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        var unreadNotifications = notificationRepository.findByUserIdAndReadFalseAndDeletedFalse(currentUser.getId());

        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);

        return Map.of("updated", unreadNotifications.size());
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findById(principal.getId())
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED));
    }
}
