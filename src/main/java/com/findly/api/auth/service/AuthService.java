package com.findly.api.auth.service;

import com.findly.api.auth.dto.AuthResponse;
import com.findly.api.auth.dto.AuthUserResponse;
import com.findly.api.auth.dto.LoginRequest;
import com.findly.api.auth.dto.LogoutRequest;
import com.findly.api.auth.dto.RefreshTokenRequest;
import com.findly.api.auth.dto.RegisterRequest;
import com.findly.api.common.enums.UserRole;
import com.findly.api.common.enums.UserStatus;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.security.jwt.JwtService;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthUserResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCaseAndDeletedFalse(normalizedEmail)) {
            throw new ApiException(ErrorCode.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);

        User savedUser = userRepository.save(user);

        return AuthUserResponse.fromUser(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCaseAndDeletedFalse(normalizedEmail)
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "Invalid email or password"));

        ensureUserCanAuthenticate(user);

        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (!passwordMatches) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String oldRefreshToken = request.refreshToken().trim();

        try {
            User user = refreshTokenService.validateRefreshTokenAndGetUser(oldRefreshToken);
            ensureUserCanAuthenticate(user);

            AuthResponse response = buildAuthResponse(user);
            refreshTokenService.rotateRefreshToken(oldRefreshToken, response.refreshToken());

            return response;
        } catch (ApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }
    }

    @Transactional
    public void logout(LogoutRequest request) {
        try {
            refreshTokenService.revokeRefreshToken(request.refreshToken().trim());
        } catch (ApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }
    }

    @Transactional(readOnly = true)
    public AuthUserResponse me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findById(principal.getId())
                .filter(foundUser -> !foundUser.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED));

        return AuthUserResponse.fromUser(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                refreshTokenService.createRefreshToken(user),
                "Bearer",
                AuthUserResponse.fromUser(user)
        );
    }

    private void ensureUserCanAuthenticate(User user) {
        if (user.getStatus() != UserStatus.ACTIVE || user.isDeleted()) {
            throw new ApiException(ErrorCode.FORBIDDEN, "User account is not active");
        }
    }
}
