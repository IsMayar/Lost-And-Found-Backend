package com.findly.api.claims.dto;

import com.findly.api.claims.entity.Claim;
import com.findly.api.common.enums.ClaimStatus;
import com.findly.api.common.enums.ReportType;

import java.time.Instant;
import java.util.UUID;

public record ClaimResponse(
        UUID id,
        UUID reportId,
        String reportTitle,
        ReportType reportType,
        UUID claimantId,
        String claimantName,
        String claimantEmail,
        ClaimStatus status,
        String message,
        String proofText,
        Instant createdAt,
        Instant updatedAt
) {

    public static ClaimResponse fromClaim(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getReport().getId(),
                claim.getReport().getTitle(),
                claim.getReport().getType(),
                claim.getClaimant().getId(),
                claim.getClaimant().getFullName(),
                claim.getClaimant().getEmail(),
                claim.getStatus(),
                claim.getMessage(),
                claim.getProofText(),
                claim.getCreatedAt(),
                claim.getUpdatedAt()
        );
    }
}
