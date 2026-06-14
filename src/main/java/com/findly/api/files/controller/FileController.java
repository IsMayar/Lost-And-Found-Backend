package com.findly.api.files.controller;

import com.findly.api.common.response.ApiResponse;
import com.findly.api.files.dto.FileUploadResponse;
import com.findly.api.files.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            HttpServletRequest servletRequest
    ) {
        FileUploadResponse response = fileStorageService.upload(file);

        return ApiResponse.success(
                "File uploaded successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}
