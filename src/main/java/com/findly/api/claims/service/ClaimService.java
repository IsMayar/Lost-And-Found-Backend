package com.findly.api.claims.service;

import com.findly.api.claims.dto.ClaimResponse;
import com.findly.api.claims.dto.CreateClaimRequest;
import com.findly.api.claims.dto.UpdateClaimStatusRequest;
import com.findly.api.claims.entity.Claim;
import com.findly.api.claims.repository.ClaimRepository;
import com.findly.api.common.enums.ClaimStatus;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.reports.entity.Report;
import com.findly.api.reports.repository.ReportRepository;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public ClaimResponse createClaim(UUID reportId, Authentication authentication, CreateClaimRequest request) {
        User claimant = getCurrentUser(authentication);
        Report report = getExistingReport(reportId);

        if (report.getOwner().getId().equals(claimant.getId())) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "You cannot claim your own report");
        }

        if (report.getStatus() != ReportStatus.ACTIVE) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Only active reports can be claimed");
        }

        if (claimRepository.existsByReportIdAndClaimantIdAndDeletedFalse(reportId, claimant.getId())) {
            throw new ApiException(ErrorCode.CONFLICT, "You already submitted a claim for this report");
        }

        Claim claim = new Claim();
        claim.setReport(report);
        claim.setClaimant(claimant);
        claim.setStatus(ClaimStatus.PENDING);
        claim.setMessage(request.message().trim());
        claim.setProofText(cleanNullable(request.proofText()));

        return ClaimResponse.fromClaim(claimRepository.save(claim));
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getMyClaims(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        return claimRepository.findByClaimantIdAndDeletedFalseOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(ClaimResponse::fromClaim)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getReportClaims(UUID reportId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Report report = getExistingReport(reportId);

        if (!report.getOwner().getId().equals(currentUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN, "You are not allowed to view claims for this report");
        }

        return claimRepository.findByReportIdAndDeletedFalseOrderByCreatedAtDesc(reportId)
                .stream()
                .map(ClaimResponse::fromClaim)
                .toList();
    }

    @Transactional
    public ClaimResponse updateClaimStatus(UUID claimId, Authentication authentication, UpdateClaimStatusRequest request) {
        User currentUser = getCurrentUser(authentication);
        Claim claim = claimRepository.findByIdAndDeletedFalse(claimId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Claim not found"));

        boolean isReportOwner = claim.getReport().getOwner().getId().equals(currentUser.getId());
        boolean isClaimant = claim.getClaimant().getId().equals(currentUser.getId());

        if (request.status() == ClaimStatus.CANCELLED) {
            if (!isClaimant) {
                throw new ApiException(ErrorCode.FORBIDDEN, "Only claimant can cancel this claim");
            }

            claim.setStatus(ClaimStatus.CANCELLED);
            return ClaimResponse.fromClaim(claimRepository.save(claim));
        }

        if (!isReportOwner) {
            throw new ApiException(ErrorCode.FORBIDDEN, "Only report owner can update claim status");
        }

        if (request.status() == ClaimStatus.PENDING) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Claim status cannot be changed back to pending");
        }

        claim.setStatus(request.status());

        if (request.status() == ClaimStatus.APPROVED || request.status() == ClaimStatus.RESOLVED) {
            claim.getReport().setStatus(ReportStatus.CLAIMED);
        }

        return ClaimResponse.fromClaim(claimRepository.save(claim));
    }

    private Report getExistingReport(UUID reportId) {
        return reportRepository.findById(reportId)
                .filter(report -> !report.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Report not found"));
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findById(principal.getId())
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED));
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }
}
