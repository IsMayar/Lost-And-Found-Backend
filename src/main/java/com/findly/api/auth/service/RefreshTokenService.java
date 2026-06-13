package com.findly.api.auth.service;

import com.findly.api.auth.entity.RefreshToken;
import com.findly.api.auth.repository.RefreshTokenRepository;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.security.jwt.JwtProperties;
import com.findly.api.security.jwt.JwtService;
import com.findly.api.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHashService tokenHashService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    public String createRefreshToken(User user) {
        String token = jwtService.generateRefreshToken(user);
        String tokenHash = tokenHashService.sha256(token);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60));

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    @Transactional(readOnly = true)
    public User validateRefreshTokenAndGetUser(String rawRefreshToken) {
        if (!jwtService.isRefreshToken(rawRefreshToken)) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }

        UUID tokenUserId = jwtService.extractUserId(rawRefreshToken);
        String tokenHash = tokenHashService.sha256(rawRefreshToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndDeletedFalse(tokenHash)
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "Invalid refresh token"));

        if (!refreshToken.isActive()) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Refresh token is expired or revoked");
        }

        if (!refreshToken.getUser().getId().equals(tokenUserId)) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }

        return refreshToken.getUser();
    }

    @Transactional
    public void rotateRefreshToken(String oldRawRefreshToken, String newRawRefreshToken) {
        String oldHash = tokenHashService.sha256(oldRawRefreshToken);
        String newHash = tokenHashService.sha256(newRawRefreshToken);

        RefreshToken oldToken = refreshTokenRepository.findByTokenHashAndDeletedFalse(oldHash)
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "Invalid refresh token"));

        oldToken.revoke(newHash);
        refreshTokenRepository.save(oldToken);
    }

    @Transactional
    public void revokeRefreshToken(String rawRefreshToken) {
        String tokenHash = tokenHashService.sha256(rawRefreshToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndDeletedFalse(tokenHash)
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "Invalid refresh token"));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }
}
