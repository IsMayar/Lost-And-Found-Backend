package com.findly.api.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final boolean success;
    private final int statusCode;
    private final String code;
    private final String message;
    private final Map<String, String> errors;
    private final Instant timestamp;
    private final String path;

    public static ApiErrorResponse of(
            int statusCode,
            String code,
            String message,
            Map<String, String> errors,
            String path
    ) {
        return ApiErrorResponse.builder()
                .success(false)
                .statusCode(statusCode)
                .code(code)
                .message(message)
                .errors(errors)
                .timestamp(Instant.now())
                .path(path)
                .build();
    }
}
