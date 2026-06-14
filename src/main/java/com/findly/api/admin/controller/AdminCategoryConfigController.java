package com.findly.api.admin.controller;

import com.findly.api.admin.dto.AdminCategoryConfigResponse;
import com.findly.api.admin.dto.AdminCreateCategoryConfigRequest;
import com.findly.api.admin.dto.AdminUpdateCategoryConfigRequest;
import com.findly.api.admin.service.AdminCategoryConfigService;
import com.findly.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryConfigController {

    private final AdminCategoryConfigService adminCategoryConfigService;

    @GetMapping
    public ApiResponse<List<AdminCategoryConfigResponse>> getCategoryConfigs(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String keyword,
            HttpServletRequest servletRequest
    ) {
        List<AdminCategoryConfigResponse> response = adminCategoryConfigService.getCategoryConfigs(active, keyword);

        return ApiResponse.success(
                "Admin category configs returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminCategoryConfigResponse> getCategoryConfigById(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        AdminCategoryConfigResponse response = adminCategoryConfigService.getCategoryConfigById(id);

        return ApiResponse.success(
                "Admin category config returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PostMapping
    public ApiResponse<AdminCategoryConfigResponse> createCategoryConfig(
            @Valid @RequestBody AdminCreateCategoryConfigRequest request,
            HttpServletRequest servletRequest
    ) {
        AdminCategoryConfigResponse response = adminCategoryConfigService.createCategoryConfig(request);

        return ApiResponse.success(
                "Admin category config saved successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminCategoryConfigResponse> updateCategoryConfig(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateCategoryConfigRequest request,
            HttpServletRequest servletRequest
    ) {
        AdminCategoryConfigResponse response = adminCategoryConfigService.updateCategoryConfig(id, request);

        return ApiResponse.success(
                "Admin category config updated successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategoryConfig(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        adminCategoryConfigService.deleteCategoryConfig(id);

        return ApiResponse.success(
                "Admin category config deleted successfully",
                null,
                servletRequest.getRequestURI()
        );
    }
}