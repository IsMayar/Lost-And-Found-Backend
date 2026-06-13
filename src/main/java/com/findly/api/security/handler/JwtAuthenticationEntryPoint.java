package com.findly.api.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.common.response.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ApiErrorResponse errorResponse = ApiErrorResponse.of(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                null,
                request.getRequestURI()
        );

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
