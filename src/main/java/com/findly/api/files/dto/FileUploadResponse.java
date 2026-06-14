package com.findly.api.files.dto;

public record FileUploadResponse(
        String fileName,
        String originalName,
        String contentType,
        long sizeBytes,
        String url
) {
}
