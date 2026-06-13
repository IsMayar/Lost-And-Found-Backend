package com.findly.api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Bad request"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Validation failed"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication is required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "You do not have permission to perform this action"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "Resource not found"),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT", "Resource conflict"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Internal server error");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String code, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
