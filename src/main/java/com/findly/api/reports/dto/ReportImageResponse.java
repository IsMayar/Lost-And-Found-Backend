package com.findly.api.reports.dto;

import com.findly.api.reports.entity.ReportImage;

import java.time.Instant;
import java.util.UUID;

public record ReportImageResponse(
        UUID id,
        UUID reportId,
        String url,
        String originalName,
        String contentType,
        Long sizeBytes,
        int sortOrder,
        boolean primaryImage,
        Instant createdAt
) {

    public static ReportImageResponse fromImage(ReportImage image) {
        return new ReportImageResponse(
                image.getId(),
                image.getReport().getId(),
                image.getUrl(),
                image.getOriginalName(),
                image.getContentType(),
                image.getSizeBytes(),
                image.getSortOrder(),
                image.isPrimaryImage(),
                image.getCreatedAt()
        );
    }
}
