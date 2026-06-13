package com.findly.api.reports.service;

import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.reports.dto.CreateReportRequest;
import com.findly.api.reports.dto.ReportResponse;
import com.findly.api.reports.dto.ReportSearchRequest;
import com.findly.api.reports.dto.UpdateReportRequest;
import com.findly.api.reports.dto.UpdateReportStatusRequest;
import com.findly.api.reports.entity.Report;
import com.findly.api.reports.repository.ReportRepository;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportResponse createReport(Authentication authentication, CreateReportRequest request) {
        User owner = getCurrentUser(authentication);

        Report report = new Report();
        report.setOwner(owner);
        report.setType(request.type());
        report.setCategory(request.category());
        report.setStatus(ReportStatus.ACTIVE);
        report.setTitle(request.title().trim());
        report.setDescription(request.description().trim());
        report.setLocationText(cleanNullable(request.locationText()));
        report.setCity(cleanNullable(request.city()));
        report.setCountry(cleanNullable(request.country()));
        report.setEventDate(request.eventDate());
        report.setColor(cleanNullable(request.color()));
        report.setBrand(cleanNullable(request.brand()));
        report.setPrivateHint(cleanNullable(request.privateHint()));
        report.setContactName(cleanNullable(request.contactName()));
        report.setContactPhone(cleanNullable(request.contactPhone()));
        report.setContactEmail(cleanNullable(request.contactEmail()));
        report.setVerified(false);

        Report savedReport = reportRepository.save(report);

        return ReportResponse.fromReport(savedReport);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> searchReports(ReportSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.safePage() - 1,
                request.safeSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<Report> specification = buildSearchSpecification(request);

        Page<ReportResponse> reports = reportRepository.findAll(specification, pageable)
                .map(ReportResponse::fromReport);

        return PageResponse.fromPage(reports);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportById(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .filter(foundReport -> !foundReport.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Report not found"));

        return ReportResponse.fromReport(report);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getMyReports(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        return reportRepository.findByOwnerIdAndDeletedFalseOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(ReportResponse::fromReport)
                .toList();
    }

    @Transactional
    public ReportResponse updateReport(UUID reportId, Authentication authentication, UpdateReportRequest request) {
        User currentUser = getCurrentUser(authentication);
        Report report = getOwnedReport(reportId, currentUser);

        if (request.category() != null) {
            report.setCategory(request.category());
        }

        if (request.title() != null && !request.title().isBlank()) {
            report.setTitle(request.title().trim());
        }

        if (request.description() != null && !request.description().isBlank()) {
            report.setDescription(request.description().trim());
        }

        if (request.locationText() != null) {
            report.setLocationText(cleanNullable(request.locationText()));
        }

        if (request.city() != null) {
            report.setCity(cleanNullable(request.city()));
        }

        if (request.country() != null) {
            report.setCountry(cleanNullable(request.country()));
        }

        if (request.eventDate() != null) {
            report.setEventDate(request.eventDate());
        }

        if (request.color() != null) {
            report.setColor(cleanNullable(request.color()));
        }

        if (request.brand() != null) {
            report.setBrand(cleanNullable(request.brand()));
        }

        if (request.privateHint() != null) {
            report.setPrivateHint(cleanNullable(request.privateHint()));
        }

        if (request.contactName() != null) {
            report.setContactName(cleanNullable(request.contactName()));
        }

        if (request.contactPhone() != null) {
            report.setContactPhone(cleanNullable(request.contactPhone()));
        }

        if (request.contactEmail() != null) {
            report.setContactEmail(cleanNullable(request.contactEmail()));
        }

        Report savedReport = reportRepository.save(report);

        return ReportResponse.fromReport(savedReport);
    }

    @Transactional
    public ReportResponse updateReportStatus(UUID reportId, Authentication authentication, UpdateReportStatusRequest request) {
        User currentUser = getCurrentUser(authentication);
        Report report = getOwnedReport(reportId, currentUser);

        if (request.status() == ReportStatus.DRAFT || request.status() == ReportStatus.PENDING_REVIEW || request.status() == ReportStatus.REJECTED) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "This status cannot be set by user");
        }

        report.setStatus(request.status());

        Report savedReport = reportRepository.save(report);

        return ReportResponse.fromReport(savedReport);
    }

    @Transactional
    public void deleteReport(UUID reportId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Report report = getOwnedReport(reportId, currentUser);

        report.markDeleted();
        reportRepository.save(report);
    }

    private Report getOwnedReport(UUID reportId, User currentUser) {
        Report report = reportRepository.findById(reportId)
                .filter(foundReport -> !foundReport.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Report not found"));

        if (!report.getOwner().getId().equals(currentUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN, "You are not allowed to modify this report");
        }

        return report;
    }

    private Specification<Report> buildSearchSpecification(ReportSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

            if (request.type() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.type()));
            }

            if (request.category() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), request.category()));
            }

            if (request.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.status()));
            }

            String city = cleanNullable(request.city());
            if (city != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("city")),
                        "%" + city.toLowerCase() + "%"
                ));
            }

            String keyword = cleanNullable(request.keyword());
            if (keyword != null) {
                String pattern = "%" + keyword.toLowerCase() + "%";

                Predicate titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern);
                Predicate descriptionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
                Predicate locationLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("locationText")), pattern);
                Predicate brandLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), pattern);
                Predicate colorLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("color")), pattern);

                predicates.add(criteriaBuilder.or(
                        titleLike,
                        descriptionLike,
                        locationLike,
                        brandLike,
                        colorLike
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
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
