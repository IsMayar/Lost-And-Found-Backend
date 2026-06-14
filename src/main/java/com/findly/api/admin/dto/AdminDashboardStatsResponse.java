package com.findly.api.admin.dto;

public record AdminDashboardStatsResponse(
        long totalUsers,
        long activeUsers,
        long suspendedUsers,
        long totalReports,
        long activeReports,
        long claimedReports,
        long resolvedReports,
        long verifiedReports,
        long totalClaims,
        long pendingClaims,
        long approvedClaims,
        long rejectedClaims,
        long unreadNotifications
) {
}