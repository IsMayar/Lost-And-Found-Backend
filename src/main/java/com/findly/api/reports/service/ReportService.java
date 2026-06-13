package com.findly.api.reports.service;

import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.reports.dto.*;
import com.findly.api.reports.entity.Report;
import com.findly.api.reports.entity.ReportImage;
import com.findly.api.reports.repository.ReportImageRepository;
import com.findly.api.reports.repository.ReportRepository;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final int MAX_IMAGES_PER_REPORT = 10;

    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;
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

        return ReportResponse.fromReport(reportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> searchReports(ReportSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.safePage() - 1,
                request.safeSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ReportResponse> reports = reportRepository.findAll(buildSearchSpecification(request), pageable)
                .map(ReportResponse::fromReport);

        return PageResponse.fromPage(reports);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportById(UUID reportId) {
        Report report = getExistingReport(reportId);
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

        if (request.category() != null) report.setCategory(request.category());
        if (request.title() != null && !request.title().isBlank()) report.setTitle(request.title().trim());
        if (request.description() != null && !request.description().isBlank()) report.setDescription(request.description().trim());
        if (request.locationText() != null) report.setLocationText(cleanNullable(request.locationText()));
        if (request.city() != null) report.setCity(cleanNullable(request.city()));
        if (request.country() != null) report.setCountry(cleanNullable(request.country()));
        if (request.eventDate() != null) report.setEventDate(request.eventDate());
        if (request.color() != null) report.setColor(cleanNullable(request.color()));
        if (request.brand() != null) report.setBrand(cleanNullable(request.brand()));
        if (request.privateHint() != null) report.setPrivateHint(cleanNullable(request.privateHint()));
        if (request.contactName() != null) report.setContactName(cleanNullable(request.contactName()));
        if (request.contactPhone() != null) report.setContactPhone(cleanNullable(request.contactPhone()));
        if (request.contactEmail() != null) report.setContactEmail(cleanNullable(request.contactEmail()));

        return ReportResponse.fromReport(reportRepository.save(report));
    }

    @Transactional
    public ReportResponse updateReportStatus(UUID reportId, Authentication authentication, UpdateReportStatusRequest request) {
        User currentUser = getCurrentUser(authentication);
        Report report = getOwnedReport(reportId, currentUser);

        if (request.status() == ReportStatus.DRAFT || request.status() == ReportStatus.PENDING_REVIEW || request.status() == ReportStatus.REJECTED) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "This status cannot be set by user");
        }

        report.setStatus(request.status());

        return ReportResponse.fromReport(reportRepository.save(report));
    }

    @Transactional
    public void deleteReport(UUID reportId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Report report = getOwnedReport(reportId, currentUser);

        report.markDeleted();
        reportRepository.save(report);
    }

    @Transactional
    public ReportImageResponse addReportImage(UUID reportId, Authentication authentication, AddReportImageRequest request) {
        User currentUser = getCurrentUser(authentication);
        Report report = getOwnedReport(reportId, currentUser);

        long imageCount = reportImageRepository.countByReportIdAndDeletedFalse(reportId);
        if (imageCount >= MAX_IMAGES_PER_REPORT) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Maximum report image limit reached");
        }

        ReportImage image = new ReportImage();
        image.setReport(report);
        image.setUrl(request.url().trim());
        image.setOriginalName(cleanNullable(request.originalName()));
        image.setContentType(cleanNullable(request.contentType()));
        image.setSizeBytes(request.sizeBytes());
        image.setSortOrder(request.sortOrder() == null ? (int) imageCount : request.sortOrder());
        image.setPrimaryImage(Boolean.TRUE.equals(request.primaryImage()) || imageCount == 0);

        return ReportImageResponse.fromImage(reportImageRepository.save(image));
    }

    @Transactional(readOnly = true)
    public List<ReportImageResponse> getReportImages(UUID reportId) {
        getExistingReport(reportId);

        return reportImageRepository.findByReportIdAndDeletedFalseOrderBySortOrderAscCreatedAtAsc(reportId)
                .stream()
                .map(ReportImageResponse::fromImage)
                .toList();
    }

    @Transactional
    public void deleteReportImage(UUID reportId, UUID imageId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        getOwnedReport(reportId, currentUser);

        ReportImage image = reportImageRepository.findByIdAndReportIdAndDeletedFalse(imageId, reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Report image not found"));

        image.markDeleted();
        reportImageRepository.save(image);
    }

    private Report getExistingReport(UUID reportId) {
        return reportRepository.findById(reportId)
                .filter(report -> !report.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Report not found"));
    }

    private Report getOwnedReport(UUID reportId, User currentUser) {
        Report report = getExistingReport(reportId);

        if (!report.getOwner().getId().equals(currentUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN, "You are not allowed to modify this report");
        }

        return report;
    }

    private Specification<Report> buildSearchSpecification(ReportSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

            if (request.type() != null) predicates.add(criteriaBuilder.equal(root.get("type"), request.type()));
            if (request.category() != null) predicates.add(criteriaBuilder.equal(root.get("category"), request.category()));
            if (request.status() != null) predicates.add(criteriaBuilder.equal(root.get("status"), request.status()));

            String city = cleanNullable(request.city());
            if (city != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
            }

            String keyword = cleanNullable(request.keyword());
            if (keyword != null) {
                String pattern = "%" + keyword.toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("locationText")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("color")), pattern)
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
        if (value == null) return null;

        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }
}
