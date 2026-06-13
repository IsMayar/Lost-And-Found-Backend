package com.findly.api.users.controller;

import com.findly.api.common.response.ApiResponse;
import com.findly.api.users.dto.ChangePasswordRequest;
import com.findly.api.users.dto.UpdateProfileRequest;
import com.findly.api.users.dto.UserProfileResponse;
import com.findly.api.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile(
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UserProfileResponse response = userService.getProfile(authentication);

        return ApiResponse.success(
                "Profile returned successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest servletRequest
    ) {
        UserProfileResponse response = userService.updateProfile(authentication, request);

        return ApiResponse.success(
                "Profile updated successfully",
                response,
                servletRequest.getRequestURI()
        );
    }

    @PutMapping("/change-password")
    public ApiResponse<Map<String, Boolean>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest servletRequest
    ) {
        userService.changePassword(authentication, request);

        return ApiResponse.success(
                "Password changed successfully",
                Map.of("passwordChanged", true),
                servletRequest.getRequestURI()
        );
    }
}
