package com.findly.api.admin.controller;

import com.findly.api.admin.dto.AdminAuditLogResponse;
import com.findly.api.admin.service.AdminAuditLogService;
import com.findly.api.common.enums.AdminAuditAction;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@RequiredArgsConstructor
public class AdminAuditLogController {

    private final AdminAuditLogService adminAuditLogService;

    @GetMapping
    public ApiResponse<PageResponse<AdminAuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) AdminAuditAction action,
            @RequestParam(required = false) UUID adminId,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest servletRequest
    ) {
        PageResponse<AdminAuditLogResponse> response = adminAuditLogService.getAuditLogs(
                action,
                adminId,
                targetType,
                keyword,
                page,
                size
        );

        return ApiResponse.success(
                "Admin audit logs returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminAuditLogResponse> getAuditLogById(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        AdminAuditLogResponse response = adminAuditLogService.getAuditLogById(id);

        return ApiResponse.success(
                "Admin audit log returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}