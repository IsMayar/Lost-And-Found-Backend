package com.findly.api.reports.dto;

import com.findly.api.files.dto.FileUploadResponse;

public record UploadedReportImageResponse(
        FileUploadResponse file,
        ReportImageResponse image
) {
}
