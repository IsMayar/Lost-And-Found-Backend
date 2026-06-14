package com.findly.api.admin.dto;

import com.findly.api.common.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateReportStatusRequest(

        @NotNull(message = "Report status is required")
        ReportStatus status
) {
}