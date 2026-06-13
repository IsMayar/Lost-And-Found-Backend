package com.findly.api.users.service;

import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.dto.ChangePasswordRequest;
import com.findly.api.users.dto.UpdateProfileRequest;
import com.findly.api.users.dto.UserProfileResponse;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return UserProfileResponse.fromUser(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Authentication authentication, UpdateProfileRequest request) {
        User user = getCurrentUser(authentication);

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName().trim());
        }

        if (request.phone() != null) {
            String phone = request.phone().trim();
            user.setPhone(phone.isBlank() ? null : phone);
        }

        if (request.avatarUrl() != null) {
            String avatarUrl = request.avatarUrl().trim();
            user.setAvatarUrl(avatarUrl.isBlank() ? null : avatarUrl);
        }

        User savedUser = userRepository.save(user);

        return UserProfileResponse.fromUser(savedUser);
    }

    @Transactional
    public void changePassword(Authentication authentication, ChangePasswordRequest request) {
        User user = getCurrentUser(authentication);

        boolean currentPasswordMatches = passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash()
        );

        if (!currentPasswordMatches) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findById(principal.getId())
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED));
    }
}
