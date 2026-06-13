package com.findly.api.reports.dto;

import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ReportSearchRequest(
        ReportType type,
        ReportCategory category,
        ReportStatus status,

        @Size(max = 120, message = "City must not exceed 120 characters")
        String city,

        @Size(max = 120, message = "Keyword must not exceed 120 characters")
        String keyword,

        @Min(value = 1, message = "Page must be greater than or equal to 1")
        Integer page,

        @Min(value = 1, message = "Size must be greater than or equal to 1")
        @Max(value = 100, message = "Size must not exceed 100")
        Integer size
) {
    public int safePage() {
        return page == null || page < 1 ? 1 : page;
    }

    public int safeSize() {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }
}
