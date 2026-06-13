package com.findly.api.users.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters")
        String fullName,

        @Size(max = 40, message = "Phone must not exceed 40 characters")
        String phone,

        @Size(max = 255, message = "Avatar URL must not exceed 255 characters")
        String avatarUrl
) {
}
