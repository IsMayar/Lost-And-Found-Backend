package com.findly.api.admin.dto;

import com.findly.api.common.enums.ContactRequestStatus;
import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.contactrequests.entity.ReportContactRequest;
import com.findly.api.reports.entity.Report;

import java.time.Instant;
import java.util.UUID;

public record AdminContactRequestResponse(
        UUID id,
        ContactRequestStatus status,
        String message,
        String responseMessage,
        Instant respondedAt,

        UUID reportId,
        String reportTitle,
        ReportType reportType,
        ReportCategory reportCategory,
        ReportStatus reportStatus,

        UUID requesterId,
        String requesterName,
        String requesterEmail,

        UUID ownerId,
        String ownerName,
        String ownerEmail,

        Instant createdAt,
        Instant updatedAt
) {

    public static AdminContactRequestResponse fromContactRequest(ReportContactRequest contactRequest) {
        Report report = contactRequest.getReport();

        return new AdminContactRequestResponse(
                contactRequest.getId(),
                contactRequest.getStatus(),
                contactRequest.getMessage(),
                contactRequest.getResponseMessage(),
                contactRequest.getRespondedAt(),

                report.getId(),
                report.getTitle(),
                report.getType(),
                report.getCategory(),
                report.getStatus(),

                contactRequest.getRequester().getId(),
                contactRequest.getRequester().getFullName(),
                contactRequest.getRequester().getEmail(),

                contactRequest.getOwner().getId(),
                contactRequest.getOwner().getFullName(),
                contactRequest.getOwner().getEmail(),

                contactRequest.getCreatedAt(),
                contactRequest.getUpdatedAt()
        );
    }
}