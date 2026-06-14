package com.findly.api.contactrequests.dto;

import jakarta.validation.constraints.Size;

public record CreateContactRequestRequest(
        @Size(max = 2000, message = "Message must be at most 2000 characters")
        String message
) {
}