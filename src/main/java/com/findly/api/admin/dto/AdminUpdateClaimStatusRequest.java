package com.findly.api.admin.dto;

import com.findly.api.common.enums.ClaimStatus;
import jakarta.validation.constraints.NotNull;

public record AdminUpdateClaimStatusRequest(
    @NotNull(message = "Claim status is required")
    ClaimStatus status
){}