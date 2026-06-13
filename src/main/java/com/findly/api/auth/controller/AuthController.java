package com.findly.api.auth.controller;

import com.findly.api.auth.dto.AuthUserResponse;
import com.findly.api.auth.dto.RegisterRequest;
import com.findly.api.auth.service.AuthService;
import com.findly.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthUserResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest servletRequest
    ) {
        AuthUserResponse response = authService.register(request);

        return ApiResponse.success(
                HttpStatus.CREATED.value(),
                "User registered successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}
