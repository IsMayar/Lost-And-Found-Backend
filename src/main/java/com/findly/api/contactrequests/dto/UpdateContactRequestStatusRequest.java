package com.findly.api.contactrequests.dto;

import com.findly.api.common.enums.ContactRequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateContactRequestStatusRequest(
        @NotNull(message = "Status is required")
        ContactRequestStatus status,

        @Size(max = 2000, message = "Response message must be at most 2000 characters")
        String responseMessage
) {
}