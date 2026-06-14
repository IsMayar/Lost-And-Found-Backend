package com.findly.api.savedreports.service;

import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.reports.entity.Report;
import com.findly.api.reports.repository.ReportRepository;
import com.findly.api.savedreports.dto.SavedReportResponse;
import com.findly.api.savedreports.entity.SavedReport;
import com.findly.api.savedreports.repository.SavedReportRepository;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavedReportService {

    private final SavedReportRepository savedReportRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public SavedReportResponse saveReport(UUID reportId, UserPrincipal principal) {
        User user = getCurrentUser(principal);
        Report report = getReport(reportId);

        return savedReportRepository.findByUserAndReportAndDeletedFalse(user, report)
                .map(SavedReportResponse::fromSavedReport)
                .orElseGet(() -> {
                    SavedReport savedReport = new SavedReport();
                    savedReport.setUser(user);
                    savedReport.setReport(report);

                    SavedReport saved = savedReportRepository.save(savedReport);
                    return SavedReportResponse.fromSavedReport(saved);
                });
    }

    @Transactional
    public void unsaveReport(UUID reportId, UserPrincipal principal) {
        User user = getCurrentUser(principal);
        Report report = getReport(reportId);

        SavedReport savedReport = savedReportRepository.findByUserAndReportAndDeletedFalse(user, report)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Saved report not found"));

        savedReport.markDeleted();
        savedReportRepository.save(savedReport);
    }

    @Transactional(readOnly = true)
    public List<SavedReportResponse> getMySavedReports(UserPrincipal principal) {
        User user = getCurrentUser(principal);

        return savedReportRepository.findByUserAndDeletedFalseOrderByCreatedAtDesc(user)
                .stream()
                .filter(savedReport -> savedReport.getReport() != null && !savedReport.getReport().isDeleted())
                .map(SavedReportResponse::fromSavedReport)
                .toList();
    }

    private User getCurrentUser(UserPrincipal principal) {
        if (principal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Authentication is required");
        }

        return userRepository.findById(principal.getId())
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "User not found"));
    }

    private Report getReport(UUID reportId) {
        return reportRepository.findById(reportId)
                .filter(report -> !report.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Report not found"));
    }
}