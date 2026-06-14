package com.findly.api.admin.service;

import com.findly.api.admin.dto.*;
import com.findly.api.claims.entity.Claim;
import com.findly.api.claims.repository.ClaimRepository;
import com.findly.api.common.enums.*;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.notifications.repository.NotificationRepository;
import com.findly.api.reports.entity.Report;
import com.findly.api.reports.repository.ReportRepository;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ClaimRepository claimRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public AdminDashboardStatsResponse getDashboardStats() {
        return new AdminDashboardStatsResponse(
                userRepository.countByDeletedFalse(),
                userRepository.countByStatusAndDeletedFalse(UserStatus.ACTIVE),
                userRepository.countByStatusAndDeletedFalse(UserStatus.SUSPENDED),
                reportRepository.countByDeletedFalse(),
                reportRepository.countByStatusAndDeletedFalse(ReportStatus.ACTIVE),
                reportRepository.countByStatusAndDeletedFalse(ReportStatus.CLAIMED),
                reportRepository.countByStatusAndDeletedFalse(ReportStatus.RESOLVED),
                reportRepository.countByVerifiedAndDeletedFalse(true),
                claimRepository.countByDeletedFalse(),
                claimRepository.countByStatusAndDeletedFalse(ClaimStatus.PENDING),
                claimRepository.countByStatusAndDeletedFalse(ClaimStatus.APPROVED),
                claimRepository.countByStatusAndDeletedFalse(ClaimStatus.REJECTED),
                notificationRepository.countByReadFalseAndDeletedFalse()
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> getUsers(String keyword, UserStatus status, Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<AdminUserResponse> users = userRepository.findAll(buildUserSpecification(keyword, status), pageable)
                .map(AdminUserResponse::fromUser);

        return PageResponse.fromPage(users);
    }

    @Transactional
    public AdminUserResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .filter(foundUser -> !foundUser.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "User not found"));

        user.setStatus(request.status());

        if (request.status() == UserStatus.DELETED) {
            user.markDeleted();
        }

        return AdminUserResponse.fromUser(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminReportResponse> getReports(
            ReportType type,
            ReportCategory category,
            ReportStatus status,
            Boolean verified,
            String keyword,
            Integer page,
            Integer size
    ) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<AdminReportResponse> reports = reportRepository
                .findAll(buildReportSpecification(type, category, status, verified, keyword), pageable)
                .map(AdminReportResponse::fromReport);

        return PageResponse.fromPage(reports);
    }

    @Transactional
    public AdminReportResponse updateReportStatus(UUID reportId, UpdateReportStatusRequest request) {
        Report report = reportRepository.findById(reportId)
                .filter(foundReport -> !foundReport.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Report not found"));

        report.setStatus(request.status());

        return AdminReportResponse.fromReport(reportRepository.save(report));
    }

    @Transactional
    public AdminReportResponse updateReportVerification(UUID reportId, UpdateReportVerificationRequest request) {
        Report report = reportRepository.findById(reportId)
                .filter(foundReport -> !foundReport.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Report not found"));

        report.setVerified(Boolean.TRUE.equals(request.verified()));

        return AdminReportResponse.fromReport(reportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminClaimResponse> getClaims(
            ClaimStatus status,
            UUID reportId,
            UUID claimantId,
            String keyword,
            Integer page,
            Integer size
    ) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<AdminClaimResponse> claims = claimRepository
                .findAll(buildClaimSpecification(status, reportId, claimantId, keyword), pageable)
                .map(AdminClaimResponse::fromClaim);

        return PageResponse.fromPage(claims);
    }

    @Transactional(readOnly = true)
    public AdminClaimResponse getClaimById(UUID claimId) {
        Claim claim = claimRepository.findByIdAndDeletedFalse(claimId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Claim not found"));

        return AdminClaimResponse.fromClaim(claim);
    }

    @Transactional
    public AdminClaimResponse updateClaimStatus(UUID claimId, AdminUpdateClaimStatusRequest request) {
        Claim claim = claimRepository.findByIdAndDeletedFalse(claimId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Claim not found"));

        claim.setStatus(request.status());

        if (request.status() == ClaimStatus.APPROVED || request.status() == ClaimStatus.RESOLVED) {
            claim.getReport().setStatus(ReportStatus.CLAIMED);
        }

        if (request.status() == ClaimStatus.REJECTED || request.status() == ClaimStatus.CANCELLED) {
            if (claim.getReport().getStatus() == ReportStatus.CLAIMED) {
                claim.getReport().setStatus(ReportStatus.ACTIVE);
            }
        }

        return AdminClaimResponse.fromClaim(claimRepository.save(claim));
    }

    private Specification<User> buildUserSpecification(String keyword, UserStatus status) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            String cleanedKeyword = cleanNullable(keyword);
            if (cleanedKeyword != null) {
                String pattern = "%" + cleanedKeyword.toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), pattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Report> buildReportSpecification(
            ReportType type,
            ReportCategory category,
            ReportStatus status,
            Boolean verified,
            String keyword
    ) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (verified != null) {
                predicates.add(criteriaBuilder.equal(root.get("verified"), verified));
            }

            String cleanedKeyword = cleanNullable(keyword);
            if (cleanedKeyword != null) {
                String pattern = "%" + cleanedKeyword.toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("locationText")), pattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Claim> buildClaimSpecification(
            ClaimStatus status,
            UUID reportId,
            UUID claimantId,
            String keyword
    ) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (reportId != null) {
                predicates.add(criteriaBuilder.equal(root.get("report").get("id"), reportId));
            }

            if (claimantId != null) {
                predicates.add(criteriaBuilder.equal(root.get("claimant").get("id"), claimantId));
            }

            String cleanedKeyword = cleanNullable(keyword);
            if (cleanedKeyword != null) {
                String pattern = "%" + cleanedKeyword.toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("message")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("proofText")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("report").get("title")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("claimant").get("fullName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("claimant").get("email")), pattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }
}