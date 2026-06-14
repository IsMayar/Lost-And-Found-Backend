package com.findly.api.admin.controller;

import com.findly.api.admin.dto.AdminDashboardOverviewResponse;
import com.findly.api.admin.service.AdminDashboardService;
import com.findly.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/overview")
    public ApiResponse<AdminDashboardOverviewResponse> getOverview(
            @RequestParam(required = false) Integer limit,
            HttpServletRequest servletRequest
    ) {
        AdminDashboardOverviewResponse response = adminDashboardService.getOverview(limit);

        return ApiResponse.success(
                "Admin dashboard overview returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}