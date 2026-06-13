package com.findly.api.common.exception;

import com.findly.api.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiErrorResponse response = ApiErrorResponse.of(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                exception.getMessage(),
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;

        ApiErrorResponse response = ApiErrorResponse.of(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                errors,
                request.getRequestURI()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            AuthenticationCredentialsNotFoundException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ApiErrorResponse response = ApiErrorResponse.of(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;

        ApiErrorResponse response = ApiErrorResponse.of(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        ApiErrorResponse response = ApiErrorResponse.of(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}
