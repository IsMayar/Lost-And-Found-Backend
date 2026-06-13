package com.findly.api.reports.dto;

import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateReportRequest(

        @NotNull(message = "Report type is required")
        ReportType type,

        @NotNull(message = "Category is required")
        ReportCategory category,

        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 160, message = "Title must be between 3 and 160 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
        String description,

        @Size(max = 255, message = "Location text must not exceed 255 characters")
        String locationText,

        @Size(max = 120, message = "City must not exceed 120 characters")
        String city,

        @Size(max = 120, message = "Country must not exceed 120 characters")
        String country,

        LocalDate eventDate,

        @Size(max = 80, message = "Color must not exceed 80 characters")
        String color,

        @Size(max = 120, message = "Brand must not exceed 120 characters")
        String brand,

        @Size(max = 5000, message = "Private hint must not exceed 5000 characters")
        String privateHint,

        @Size(max = 120, message = "Contact name must not exceed 120 characters")
        String contactName,

        @Size(max = 40, message = "Contact phone must not exceed 40 characters")
        String contactPhone,

        @Email(message = "Contact email must be valid")
        @Size(max = 180, message = "Contact email must not exceed 180 characters")
        String contactEmail
) {
}
