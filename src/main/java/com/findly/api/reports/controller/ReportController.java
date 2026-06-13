package com.findly.api.reports.controller;

import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.common.response.ApiResponse;
import com.findly.api.reports.dto.CreateReportRequest;
import com.findly.api.reports.dto.ReportResponse;
import com.findly.api.reports.dto.ReportSearchRequest;
import com.findly.api.reports.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ApiResponse<PageResponse<ReportResponse>> searchReports(
            @RequestParam(required = false) ReportType type,
            @RequestParam(required = false) ReportCategory category,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest servletRequest
    ) {
        ReportSearchRequest request = new ReportSearchRequest(
                type,
                category,
                status,
                city,
                keyword,
                page,
                size
        );

        PageResponse<ReportResponse> response = reportService.searchReports(request);

        return ApiResponse.success(
                "Reports returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReportResponse> createReport(
            Authentication authentication,
            @Valid @RequestBody CreateReportRequest request,
            HttpServletRequest servletRequest
    ) {
        ReportResponse response = reportService.createReport(authentication, request);

        return ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Report created successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/my")
    public ApiResponse<List<ReportResponse>> getMyReports(
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        List<ReportResponse> response = reportService.getMyReports(authentication);

        return ApiResponse.success(
                "My reports returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportResponse> getReportById(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        ReportResponse response = reportService.getReportById(id);

        return ApiResponse.success(
                "Report returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}
