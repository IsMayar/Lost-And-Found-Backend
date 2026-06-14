package com.findly.api.admin.dto;

import com.findly.api.admin.entity.AdminAuditLog;
import com.findly.api.common.enums.AdminAuditAction;

import java.time.Instant;
import java.util.UUID;

public record AdminAuditLogResponse(
        UUID id,
        UUID adminId,
        String adminName,
        String adminEmail,
        AdminAuditAction action,
        String targetType,
        UUID targetId,
        String description,
        Instant createdAt
) {

    public static AdminAuditLogResponse fromAuditLog(AdminAuditLog auditLog) {
        return new AdminAuditLogResponse(
                auditLog.getId(),
                auditLog.getAdmin().getId(),
                auditLog.getAdmin().getFullName(),
                auditLog.getAdmin().getEmail(),
                auditLog.getAction(),
                auditLog.getTargetType(),
                auditLog.getTargetId(),
                auditLog.getDescription(),
                auditLog.getCreatedAt()
        );
    }
}