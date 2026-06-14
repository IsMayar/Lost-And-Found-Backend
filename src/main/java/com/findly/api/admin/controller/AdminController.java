package com.findly.api.admin.controller;

import com.findly.api.admin.dto.*;
import com.findly.api.admin.service.AdminService;
import com.findly.api.common.enums.ClaimStatus;
import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.common.enums.UserStatus;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard/stats")
    public ApiResponse<AdminDashboardStatsResponse> getDashboardStats(
            HttpServletRequest servletRequest
    ) {
        AdminDashboardStatsResponse response = adminService.getDashboardStats();

        return ApiResponse.success(
                "Admin dashboard stats returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/users")
    public ApiResponse<PageResponse<AdminUserResponse>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest servletRequest
    ) {
        PageResponse<AdminUserResponse> response = adminService.getUsers(keyword, status, page, size);

        return ApiResponse.success(
                "Admin users returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PatchMapping("/users/{id}/status")
    public ApiResponse<AdminUserResponse> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request,
            HttpServletRequest servletRequest
    ) {
        AdminUserResponse response = adminService.updateUserStatus(id, request);

        return ApiResponse.success(
                "User status updated successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/reports")
    public ApiResponse<PageResponse<AdminReportResponse>> getReports(
            @RequestParam(required = false) ReportType type,
            @RequestParam(required = false) ReportCategory category,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest servletRequest
    ) {
        PageResponse<AdminReportResponse> response = adminService.getReports(
                type,
                category,
                status,
                verified,
                keyword,
                page,
                size
        );

        return ApiResponse.success(
                "Admin reports returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PatchMapping("/reports/{id}/status")
    public ApiResponse<AdminReportResponse> updateReportStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReportStatusRequest request,
            HttpServletRequest servletRequest
    ) {
        AdminReportResponse response = adminService.updateReportStatus(id, request);

        return ApiResponse.success(
                "Report status updated successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PatchMapping("/reports/{id}/verify")
    public ApiResponse<AdminReportResponse> updateReportVerification(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReportVerificationRequest request,
            HttpServletRequest servletRequest
    ) {
        AdminReportResponse response = adminService.updateReportVerification(id, request);

        return ApiResponse.success(
                "Report verification updated successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/claims")
    public ApiResponse<PageResponse<AdminClaimResponse>> getClaims(
            @RequestParam(required = false) ClaimStatus status,
            @RequestParam(required = false) UUID reportId,
            @RequestParam(required = false) UUID claimantId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest servletRequest
    ) {
        PageResponse<AdminClaimResponse> response = adminService.getClaims(
                status,
                reportId,
                claimantId,
                keyword,
                page,
                size
        );

        return ApiResponse.success(
                "Admin claims returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/claims/{id}")
    public ApiResponse<AdminClaimResponse> getClaimById(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        AdminClaimResponse response = adminService.getClaimById(id);

        return ApiResponse.success(
                "Admin claim returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PatchMapping("/claims/{id}/status")
    public ApiResponse<AdminClaimResponse> updateClaimStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateClaimStatusRequest request,
            HttpServletRequest servletRequest
    ) {
        AdminClaimResponse response = adminService.updateClaimStatus(id, request);

        return ApiResponse.success(
                "Admin claim status updated successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}