package com.findly.api.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final int statusCode;
    private final String message;
    private final T data;
    private final Instant timestamp;
    private final String path;

    public static <T> ApiResponse<T> success(int statusCode, String message, T data, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data, String path) {
        return success(200, message, data, path);
    }
}
