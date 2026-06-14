package com.findly.api.admin.dto;

import com.findly.api.common.enums.ReportCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminCreateCategoryConfigRequest(
        @NotNull(message = "Category is required")
        ReportCategory category,

        @NotBlank(message = "Display name is required")
        @Size(max = 120, message = "Display name must be at most 120 characters")
        String displayName,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @Size(max = 80, message = "Icon name must be at most 80 characters")
        String iconName,

        Boolean active,

        @Min(value = 0, message = "Sort order must be zero or greater")
        Integer sortOrder
) {
}