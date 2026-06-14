package com.findly.api.admin.service;

import com.findly.api.admin.dto.AdminAuditLogResponse;
import com.findly.api.admin.entity.AdminAuditLog;
import com.findly.api.admin.repository.AdminAuditLogRepository;
import com.findly.api.common.enums.AdminAuditAction;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.common.pagination.PageResponse;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminAuditLogService {

    private final AdminAuditLogRepository adminAuditLogRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(
            AdminAuditAction action,
            String targetType,
            UUID targetId,
            String description
    ) {
        User admin = getCurrentAdminOrNull();

        if (admin == null) {
            return;
        }

        AdminAuditLog auditLog = new AdminAuditLog();
        auditLog.setAdmin(admin);
        auditLog.setAction(action);
        auditLog.setTargetType(targetType);
        auditLog.setTargetId(targetId);
        auditLog.setDescription(description);

        adminAuditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminAuditLogResponse> getAuditLogs(
            AdminAuditAction action,
            UUID adminId,
            String targetType,
            String keyword,
            Integer page,
            Integer size
    ) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        List<AdminAuditLogResponse> filteredLogs = adminAuditLogRepository
                .findByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .filter(auditLog -> matchesAction(auditLog, action))
                .filter(auditLog -> matchesAdminId(auditLog, adminId))
                .filter(auditLog -> matchesTargetType(auditLog, targetType))
                .filter(auditLog -> matchesKeyword(auditLog, keyword))
                .map(AdminAuditLogResponse::fromAuditLog)
                .toList();

        int start = Math.min((int) pageable.getOffset(), filteredLogs.size());
        int end = Math.min(start + pageable.getPageSize(), filteredLogs.size());

        Page<AdminAuditLogResponse> auditLogPage = new PageImpl<>(
                filteredLogs.subList(start, end),
                pageable,
                filteredLogs.size()
        );

        return PageResponse.fromPage(auditLogPage);
    }

    @Transactional(readOnly = true)
    public AdminAuditLogResponse getAuditLogById(UUID auditLogId) {
        AdminAuditLog auditLog = adminAuditLogRepository.findByIdAndDeletedFalse(auditLogId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Audit log not found"));

        return AdminAuditLogResponse.fromAuditLog(auditLog);
    }

    private User getCurrentAdminOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }

        return userRepository.findById(principal.getId())
                .filter(user -> !user.isDeleted())
                .orElse(null);
    }

    private boolean matchesAction(AdminAuditLog auditLog, AdminAuditAction action) {
        if (action == null) {
            return true;
        }

        return auditLog.getAction() == action;
    }

    private boolean matchesAdminId(AdminAuditLog auditLog, UUID adminId) {
        if (adminId == null) {
            return true;
        }

        return auditLog.getAdmin() != null && auditLog.getAdmin().getId().equals(adminId);
    }

    private boolean matchesTargetType(AdminAuditLog auditLog, String targetType) {
        String cleanedTargetType = cleanNullable(targetType);

        if (cleanedTargetType == null) {
            return true;
        }

        return auditLog.getTargetType() != null
                && auditLog.getTargetType().equalsIgnoreCase(cleanedTargetType);
    }

    private boolean matchesKeyword(AdminAuditLog auditLog, String keyword) {
        String cleanedKeyword = cleanNullable(keyword);

        if (cleanedKeyword == null) {
            return true;
        }

        String pattern = cleanedKeyword.toLowerCase();

        return contains(auditLog.getDescription(), pattern)
                || contains(auditLog.getTargetType(), pattern)
                || contains(auditLog.getAdmin() == null ? null : auditLog.getAdmin().getFullName(), pattern)
                || contains(auditLog.getAdmin() == null ? null : auditLog.getAdmin().getEmail(), pattern);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }
}