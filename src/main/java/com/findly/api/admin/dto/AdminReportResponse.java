package com.findly.api.admin.dto;

import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.reports.entity.Report;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AdminReportResponse(
  UUID id,
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
        Instant createdAt,
        Instant updatedAt

){

   
    public static AdminReportResponse fromReport(Report report) {
        return new AdminReportResponse(
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
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}