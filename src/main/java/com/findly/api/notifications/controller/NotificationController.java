package com.findly.api.notifications.controller;

import com.findly.api.common.pagination.PageResponse;
import com.findly.api.common.response.ApiResponse;
import com.findly.api.notifications.dto.NotificationResponse;
import com.findly.api.notifications.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest servletRequest
    ) {
        PageResponse<NotificationResponse> response = notificationService.getMyNotifications(authentication, page, size);

        return ApiResponse.success(
                "Notifications returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> getUnreadCount(
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        Map<String, Long> response = notificationService.getUnreadCount(authentication);

        return ApiResponse.success(
                "Unread notifications count returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markAsRead(
            @PathVariable UUID id,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        NotificationResponse response = notificationService.markAsRead(id, authentication);

        return ApiResponse.success(
                "Notification marked as read successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PatchMapping("/read-all")
    public ApiResponse<Map<String, Integer>> markAllAsRead(
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        Map<String, Integer> response = notificationService.markAllAsRead(authentication);

        return ApiResponse.success(
                "Notifications marked as read successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}
