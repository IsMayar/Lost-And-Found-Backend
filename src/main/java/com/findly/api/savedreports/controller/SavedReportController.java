package com.findly.api.savedreports.controller;

import com.findly.api.common.response.ApiResponse;
import com.findly.api.savedreports.dto.SavedReportResponse;
import com.findly.api.savedreports.service.SavedReportService;
import com.findly.api.security.user.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class SavedReportController {

    private final SavedReportService savedReportService;

    @PostMapping("/{reportId}/save")
    public ApiResponse<SavedReportResponse> saveReport(
            @PathVariable UUID reportId,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest servletRequest
    ) {
        SavedReportResponse response = savedReportService.saveReport(reportId, principal);

        return ApiResponse.success(
                "Report saved successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @DeleteMapping("/{reportId}/save")
    public ApiResponse<Void> unsaveReport(
            @PathVariable UUID reportId,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest servletRequest
    ) {
        savedReportService.unsaveReport(reportId, principal);

        return ApiResponse.success(
                "Report removed from saved successfully",
                null,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/saved")
    public ApiResponse<List<SavedReportResponse>> getMySavedReports(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest servletRequest
    ) {
        List<SavedReportResponse> response = savedReportService.getMySavedReports(principal);

        return ApiResponse.success(
                "Saved reports returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}