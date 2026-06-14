package com.findly.api.admin.dto;

import com.findly.api.common.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(

        @NotNull(message = "User status is required")
        UserStatus status
) {
}