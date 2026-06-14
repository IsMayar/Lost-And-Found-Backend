package com.findly.api.files.service;

import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.files.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final FileStorageProperties fileStorageProperties;

    public FileUploadResponse upload(MultipartFile file) {
        validateFile(file);

        String originalName = cleanOriginalName(file.getOriginalFilename());
        String extension = getExtension(originalName);
        String storedFileName = UUID.randomUUID() + extension;

        Path uploadDir = Paths.get(fileStorageProperties.localDir()).toAbsolutePath().normalize();
        Path targetPath = uploadDir.resolve(storedFileName).normalize();

        try {
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to store uploaded file");
        }

        String publicPath = normalizePublicPath(fileStorageProperties.publicPath());
        String url = publicPath + "/" + storedFileName;

        return new FileUploadResponse(
                storedFileName,
                originalName,
                file.getContentType(),
                file.getSize(),
                url
        );
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "File size must not exceed 5MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Only JPG, PNG, and WEBP images are allowed");
        }
    }

    private String cleanOriginalName(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "upload";
        }

        return Paths.get(originalName).getFileName().toString();
    }

    private String getExtension(String originalName) {
        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex < 0) {
            return "";
        }

        return originalName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private String normalizePublicPath(String publicPath) {
        if (publicPath == null || publicPath.isBlank()) {
            return "/uploads";
        }

        String normalized = publicPath.trim();

        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }

        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }
}
