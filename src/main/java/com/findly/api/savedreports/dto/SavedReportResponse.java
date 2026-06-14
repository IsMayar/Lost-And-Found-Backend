package com.findly.api.savedreports.dto;

import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.reports.entity.Report;
import com.findly.api.savedreports.entity.SavedReport;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SavedReportResponse(
        UUID id,
        UUID reportId,
        UUID ownerId,
        String ownerName,
        String ownerEmail,
        ReportType type,
        ReportCategory category,
        ReportStatus status,
        String title,
        String description,
        String locationText,
        String city,
        String country,
        LocalDate eventDate,
        String color,
        String brand,
        boolean verified,
        Instant savedAt,
        Instant reportCreatedAt,
        Instant reportUpdatedAt
) {

    public static SavedReportResponse fromSavedReport(SavedReport savedReport) {
        Report report = savedReport.getReport();

        return new SavedReportResponse(
                savedReport.getId(),
                report.getId(),
                report.getOwner().getId(),
                report.getOwner().getFullName(),
                report.getOwner().getEmail(),
                report.getType(),
                report.getCategory(),
                report.getStatus(),
                report.getTitle(),
                report.getDescription(),
                report.getLocationText(),
                report.getCity(),
                report.getCountry(),
                report.getEventDate(),
                report.getColor(),
                report.getBrand(),
                report.isVerified(),
                savedReport.getCreatedAt(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}