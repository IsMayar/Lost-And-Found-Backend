package com.findly.api.admin.controller;

import com.findly.api.admin.dto.AdminNotificationResponse;
import com.findly.api.admin.service.AdminNotificationService;
import com.findly.api.common.enums.NotificationType;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @GetMapping
    public ApiResponse<PageResponse<AdminNotificationResponse>> getNotifications(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) Boolean read,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest servletRequest
    ) {
        PageResponse<AdminNotificationResponse> response = adminNotificationService.getNotifications(
                userId,
                type,
                read,
                keyword,
                page,
                size
        );

        return ApiResponse.success(
                "Admin notifications returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminNotificationResponse> getNotificationById(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        AdminNotificationResponse response = adminNotificationService.getNotificationById(id);

        return ApiResponse.success(
                "Admin notification returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        adminNotificationService.deleteNotification(id);

        return ApiResponse.success(
                "Admin notification deleted successfully",
                null,
                servletRequest.getRequestURI()
        );
    }
}