package com.findly.api.reports.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddReportImageRequest(

        @NotBlank(message = "Image URL is required")
        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String url,

        @Size(max = 255, message = "Original name must not exceed 255 characters")
        String originalName,

        @Size(max = 120, message = "Content type must not exceed 120 characters")
        String contentType,

        @Min(value = 1, message = "Size bytes must be greater than 0")
        Long sizeBytes,

        Integer sortOrder,

        Boolean primaryImage
) {
}
