package com.findly.api.reports.controller;

import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.common.response.ApiResponse;
import com.findly.api.reports.dto.*;
import com.findly.api.reports.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        ReportSearchRequest request = new ReportSearchRequest(type, category, status, city, keyword, page, size);
        PageResponse<ReportResponse> response = reportService.searchReports(request);

        return ApiResponse.success("Reports returned successfully", response, servletRequest.getRequestURI());
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

        return ApiResponse.success("My reports returned successfully", response, servletRequest.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportResponse> getReportById(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        ReportResponse response = reportService.getReportById(id);

        return ApiResponse.success("Report returned successfully", response, servletRequest.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<ReportResponse> updateReport(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody UpdateReportRequest request,
            HttpServletRequest servletRequest
    ) {
        ReportResponse response = reportService.updateReport(id, authentication, request);

        return ApiResponse.success("Report updated successfully", response, servletRequest.getRequestURI());
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ReportResponse> updateReportStatus(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody UpdateReportStatusRequest request,
            HttpServletRequest servletRequest
    ) {
        ReportResponse response = reportService.updateReportStatus(id, authentication, request);

        return ApiResponse.success("Report status updated successfully", response, servletRequest.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Boolean>> deleteReport(
            @PathVariable UUID id,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        reportService.deleteReport(id, authentication);

        return ApiResponse.success("Report deleted successfully", Map.of("deleted", true), servletRequest.getRequestURI());
    }

    @PostMapping("/{id}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReportImageResponse> addReportImage(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody AddReportImageRequest request,
            HttpServletRequest servletRequest
    ) {
        ReportImageResponse response = reportService.addReportImage(id, authentication, request);

        return ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Report image added successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/{id}/images")
    public ApiResponse<List<ReportImageResponse>> getReportImages(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        List<ReportImageResponse> response = reportService.getReportImages(id);

        return ApiResponse.success("Report images returned successfully", response, servletRequest.getRequestURI());
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ApiResponse<Map<String, Boolean>> deleteReportImage(
            @PathVariable UUID id,
            @PathVariable UUID imageId,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        reportService.deleteReportImage(id, imageId, authentication);

        return ApiResponse.success("Report image deleted successfully", Map.of("deleted", true), servletRequest.getRequestURI());
    }
}
