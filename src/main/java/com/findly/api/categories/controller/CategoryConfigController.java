package com.findly.api.categories.controller;

import com.findly.api.categories.dto.CategoryConfigResponse;
import com.findly.api.categories.service.CategoryConfigService;
import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryConfigController {

    private final CategoryConfigService categoryConfigService;

    @GetMapping
    public ApiResponse<List<CategoryConfigResponse>> getCategories(
            @RequestParam(required = false) String keyword,
            HttpServletRequest servletRequest
    ) {
        List<CategoryConfigResponse> response = categoryConfigService.getActiveCategories(keyword);

        return ApiResponse.success(
                "Categories returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/{category}")
    public ApiResponse<CategoryConfigResponse> getCategoryByCategory(
            @PathVariable ReportCategory category,
            HttpServletRequest servletRequest
    ) {
        CategoryConfigResponse response = categoryConfigService.getActiveCategoryByCategory(category);

        return ApiResponse.success(
                "Category returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}