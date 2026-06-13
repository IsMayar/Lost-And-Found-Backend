package com.findly.api.reports.dto;

import com.findly.api.common.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateReportStatusRequest(

        @NotNull(message = "Status is required")
        ReportStatus status
) {
}
