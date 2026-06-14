package com.findly.api.admin.service;

import com.findly.api.admin.dto.AdminClaimResponse;
import com.findly.api.admin.dto.AdminDashboardOverviewResponse;
import com.findly.api.admin.dto.AdminNotificationResponse;
import com.findly.api.admin.dto.AdminReportResponse;
import com.findly.api.claims.repository.ClaimRepository;
import com.findly.api.common.enums.ClaimStatus;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.notifications.repository.NotificationRepository;
import com.findly.api.reports.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final ReportRepository reportRepository;
    private final ClaimRepository claimRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public AdminDashboardOverviewResponse getOverview(Integer limit) {
        int safeLimit = limit == null || limit < 1 ? 5 : Math.min(limit, 20);

        PageRequest pageRequest = PageRequest.of(0, safeLimit);

        var recentReports = reportRepository.findByDeletedFalseOrderByCreatedAtDesc(pageRequest)
                .stream()
                .map(AdminReportResponse::fromReport)
                .toList();

        var recentClaims = claimRepository.findByDeletedFalseOrderByCreatedAtDesc(pageRequest)
                .stream()
                .map(AdminClaimResponse::fromClaim)
                .toList();

        var recentNotifications = notificationRepository.findByDeletedFalseOrderByCreatedAtDesc(pageRequest)
                .stream()
                .map(AdminNotificationResponse::fromNotification)
                .toList();

        var pendingReviewReports = reportRepository
                .findByStatusAndDeletedFalseOrderByCreatedAtDesc(ReportStatus.PENDING_REVIEW, pageRequest)
                .stream()
                .map(AdminReportResponse::fromReport)
                .toList();

        var pendingClaims = claimRepository
                .findByStatusAndDeletedFalseOrderByCreatedAtDesc(ClaimStatus.PENDING, pageRequest)
                .stream()
                .map(AdminClaimResponse::fromClaim)
                .toList();

        return new AdminDashboardOverviewResponse(
                recentReports,
                recentClaims,
                recentNotifications,
                pendingReviewReports,
                pendingClaims
        );
    }
}