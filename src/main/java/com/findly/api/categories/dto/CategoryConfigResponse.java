package com.findly.api.categories.dto;

import com.findly.api.categories.entity.CategoryConfig;
import com.findly.api.common.enums.ReportCategory;

import java.util.UUID;

public record CategoryConfigResponse(
        UUID id,
        ReportCategory category,
        String displayName,
        String description,
        String iconName,
        int sortOrder
) {

    public static CategoryConfigResponse fromCategoryConfig(CategoryConfig categoryConfig) {
        return new CategoryConfigResponse(
                categoryConfig.getId(),
                categoryConfig.getCategory(),
                categoryConfig.getDisplayName(),
                categoryConfig.getDescription(),
                categoryConfig.getIconName(),
                categoryConfig.getSortOrder()
        );
    }
}