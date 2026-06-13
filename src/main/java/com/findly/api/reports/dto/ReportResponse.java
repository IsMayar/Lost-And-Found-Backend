package com.findly.api.reports.dto;

import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.reports.entity.Report;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID ownerId,
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
        String contactName,
        String contactPhone,
        String contactEmail,
        boolean verified,
        Instant createdAt,
        Instant updatedAt
) {

    public static ReportResponse fromReport(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getOwner().getId(),
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
                report.getContactName(),
                report.getContactPhone(),
                report.getContactEmail(),
                report.isVerified(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}
