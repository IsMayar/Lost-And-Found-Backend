package com.findly.api.claims.dto;

import com.findly.api.common.enums.ClaimStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateClaimStatusRequest(

        @NotNull(message = "Status is required")
        ClaimStatus status
) {
}
