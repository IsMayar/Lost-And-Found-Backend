package com.findly.api.admin.dto;

import com.findly.api.claims.entity.Claim;
import com.findly.api.common.enums.ClaimStatus;
import com.findly.api.common.enums.ReportType;

import java.time.Instant;
import java.util.UUID;

public record AdminClaimResponse(
        UUID id,
        UUID reportId,
        String reportTitle,
        ReportType reportType,
        UUID reportOwnerId,
        String reportOwnerName,
        String reportOwnerEmail,
        UUID claimantId,
        String claimantName,
        String claimantEmail,
        ClaimStatus status,
        String message,
        String proofText,
        Instant createdAt,
        Instant updatedAt
) {

    public static AdminClaimResponse fromClaim(Claim claim) {
        return new AdminClaimResponse(
                claim.getId(),
                claim.getReport().getId(),
                claim.getReport().getTitle(),
                claim.getReport().getType(),
                claim.getReport().getOwner().getId(),
                claim.getReport().getOwner().getFullName(),
                claim.getReport().getOwner().getEmail(),
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