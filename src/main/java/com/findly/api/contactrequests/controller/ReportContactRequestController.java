package com.findly.api.contactrequests.controller;

import com.findly.api.common.response.ApiResponse;
import com.findly.api.contactrequests.dto.CreateContactRequestRequest;
import com.findly.api.contactrequests.dto.ReportContactRequestResponse;
import com.findly.api.contactrequests.dto.UpdateContactRequestStatusRequest;
import com.findly.api.contactrequests.service.ReportContactRequestService;
import com.findly.api.security.user.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReportContactRequestController {

    private final ReportContactRequestService contactRequestService;

    @PostMapping("/api/v1/reports/{reportId}/contact-requests")
    public ApiResponse<ReportContactRequestResponse> createContactRequest(
            @PathVariable UUID reportId,
            @Valid @RequestBody(required = false) CreateContactRequestRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest servletRequest
    ) {
        ReportContactRequestResponse response = contactRequestService.createContactRequest(
                reportId,
                request,
                principal
        );

        return ApiResponse.success(
                "Contact request submitted successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/api/v1/contact-requests/my-sent")
    public ApiResponse<List<ReportContactRequestResponse>> getMySentRequests(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest servletRequest
    ) {
        List<ReportContactRequestResponse> response = contactRequestService.getMySentRequests(principal);

        return ApiResponse.success(
                "Sent contact requests returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/api/v1/contact-requests/my-received")
    public ApiResponse<List<ReportContactRequestResponse>> getMyReceivedRequests(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest servletRequest
    ) {
        List<ReportContactRequestResponse> response = contactRequestService.getMyReceivedRequests(principal);

        return ApiResponse.success(
                "Received contact requests returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PatchMapping("/api/v1/contact-requests/{id}/status")
    public ApiResponse<ReportContactRequestResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateContactRequestStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest servletRequest
    ) {
        ReportContactRequestResponse response = contactRequestService.updateStatus(
                id,
                request,
                principal
        );

        return ApiResponse.success(
                "Contact request status updated successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}