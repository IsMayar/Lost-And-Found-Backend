package com.findly.api.auth.dto;

import com.findly.api.common.enums.UserRole;
import com.findly.api.common.enums.UserStatus;
import com.findly.api.users.entity.User;

import java.util.UUID;

public record AuthUserResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        String avatarUrl,
        UserRole role,
        UserStatus status,
        boolean emailVerified,
        boolean phoneVerified
) {

    public static AuthUserResponse fromUser(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStatus(),
                user.isEmailVerified(),
                user.isPhoneVerified()
        );
    }
}
