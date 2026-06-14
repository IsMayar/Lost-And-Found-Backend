package com.findly.api.claims.controller;

import com.findly.api.claims.dto.ClaimResponse;
import com.findly.api.claims.dto.CreateClaimRequest;
import com.findly.api.claims.dto.UpdateClaimStatusRequest;
import com.findly.api.claims.service.ClaimService;
import com.findly.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping("/reports/{reportId}/claims")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ClaimResponse> createClaim(
            @PathVariable UUID reportId,
            Authentication authentication,
            @Valid @RequestBody CreateClaimRequest request,
            HttpServletRequest servletRequest
    ) {
        ClaimResponse response = claimService.createClaim(reportId, authentication, request);

        return ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Claim submitted successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/claims/my")
    public ApiResponse<List<ClaimResponse>> getMyClaims(
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        List<ClaimResponse> response = claimService.getMyClaims(authentication);

        return ApiResponse.success(
                "My claims returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @GetMapping("/reports/{reportId}/claims")
    public ApiResponse<List<ClaimResponse>> getReportClaims(
            @PathVariable UUID reportId,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        List<ClaimResponse> response = claimService.getReportClaims(reportId, authentication);

        return ApiResponse.success(
                "Report claims returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PatchMapping("/claims/{claimId}/status")
    public ApiResponse<ClaimResponse> updateClaimStatus(
            @PathVariable UUID claimId,
            Authentication authentication,
            @Valid @RequestBody UpdateClaimStatusRequest request,
            HttpServletRequest servletRequest
    ) {
        ClaimResponse response = claimService.updateClaimStatus(claimId, authentication, request);

        return ApiResponse.success(
                "Claim status updated successfully",
                response,
                servletRequest.getRequestURI()
        );
    }
}
