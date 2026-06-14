package com.findly.api.admin.dto;

import java.util.List;

public record AdminDashboardOverviewResponse(
        List<AdminReportResponse> recentReports,
        List<AdminClaimResponse> recentClaims,
        List<AdminNotificationResponse> recentNotifications,
        List<AdminReportResponse> pendingReviewReports,
        List<AdminClaimResponse> pendingClaims
) {
}