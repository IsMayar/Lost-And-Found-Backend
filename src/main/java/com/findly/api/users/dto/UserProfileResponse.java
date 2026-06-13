package com.findly.api.users.dto;

import com.findly.api.common.enums.UserRole;
import com.findly.api.common.enums.UserStatus;
import com.findly.api.users.entity.User;

import java.util.UUID;

public record UserProfileResponse(
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

    public static UserProfileResponse fromUser(User user) {
        return new UserProfileResponse(
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
