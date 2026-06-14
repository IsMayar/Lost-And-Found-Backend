package com.findly.api.admin.controller;

import com.findly.api.admin.dto.AdminContactRequestResponse;
import com.findly.api.admin.dto.AdminUpdateContactRequestStatusRequest;
import com.findly.api.admin.service.AdminContactRequestService;
import com.findly.api.common.enums.ContactRequestStatus;
import com.findly.api.common.response.ApiResponse;
import com.findly.api.security.user.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/contact-requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminContactRequestController {

    private final AdminContactRequestService adminContactRequestService;

    @GetMapping
    public ApiResponse<Page<AdminContactRequestResponse>> getContactRequests(
            @RequestParam(required = false) ContactRequestStatus status,
            @RequestParam(required = false) UUID reportId,
            @RequestParam(required = false) UUID requesterId,
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest servletRequest
    ) {
        Page<AdminContactRequestResponse> response = adminContactRequestService.getContactRequests(
                status,
                reportId,
                requesterId,
                ownerId,
                keyword,
                page,
                size
        );

        return ApiResponse.success(
                "Admin contact requests returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminContactRequestResponse> getContactRequest(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        AdminContactRequestResponse response = adminContactRequestService.getContactRequest(id);

        return ApiResponse.success(
                "Admin contact request returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<AdminContactRequestResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateContactRequestStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest servletRequest
    ) {
        AdminContactRequestResponse response = adminContactRequestService.updateStatus(
                id,
                request,
                principal
        );

        return ApiResponse.success(
                "Admin contact request status updated successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}