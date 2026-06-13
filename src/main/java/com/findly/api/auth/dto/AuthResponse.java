package com.findly.api.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        AuthUserResponse user
) {
}
