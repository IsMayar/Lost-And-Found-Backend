package com.findly.api.admin.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateReportVerificationRequest(

        @NotNull(message = "Verified value is required")
        Boolean verified
) {
}