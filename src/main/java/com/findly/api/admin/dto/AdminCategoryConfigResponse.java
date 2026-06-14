package com.findly.api.admin.dto;

import com.findly.api.categories.entity.CategoryConfig;
import com.findly.api.common.enums.ReportCategory;

import java.time.Instant;
import java.util.UUID;

public record AdminCategoryConfigResponse(
        UUID id,
        ReportCategory category,
        String displayName,
        String description,
        String iconName,
        boolean active,
        int sortOrder,
        Instant createdAt,
        Instant updatedAt
) {

    public static AdminCategoryConfigResponse fromCategoryConfig(CategoryConfig categoryConfig) {
        return new AdminCategoryConfigResponse(
                categoryConfig.getId(),
                categoryConfig.getCategory(),
                categoryConfig.getDisplayName(),
                categoryConfig.getDescription(),
                categoryConfig.getIconName(),
                categoryConfig.isActive(),
                categoryConfig.getSortOrder(),
                categoryConfig.getCreatedAt(),
                categoryConfig.getUpdatedAt()
        );
    }
}