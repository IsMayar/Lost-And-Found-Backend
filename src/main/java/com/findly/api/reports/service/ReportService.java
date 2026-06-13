package com.findly.api.reports.service;

import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.reports.dto.CreateReportRequest;
import com.findly.api.reports.dto.ReportResponse;
import com.findly.api.reports.entity.Report;
import com.findly.api.reports.repository.ReportRepository;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
