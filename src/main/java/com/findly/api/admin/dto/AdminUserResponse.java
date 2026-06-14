package com.findly.api.admin.dto;

import com.findly.api.common.enums.UserRole;
import com.findly.api.common.enums.UserStatus;
import com.findly.api.users.entity.User;

import java.time.Instant;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        String avatarUrl,
        UserRole role,
        UserStatus status,
        boolean emailVerified,
        boolean phoneVerified,
        Instant createdAt,
        Instant updatedAt
) {

    public static AdminUserResponse fromUser(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStatus(),
                user.isEmailVerified(),
                user.isPhoneVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}