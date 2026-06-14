package com.findly.api.admin.entity;

import com.findly.api.common.entity.BaseEntity;
import com.findly.api.common.enums.AdminAuditAction;
import com.findly.api.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "admin_audit_logs",
        indexes = {
                @Index(name = "idx_admin_audit_logs_admin_id", columnList = "admin_id"),
                @Index(name = "idx_admin_audit_logs_action", columnList = "action"),
                @Index(name = "idx_admin_audit_logs_target_type", columnList = "target_type"),
                @Index(name = "idx_admin_audit_logs_target_id", columnList = "target_id"),
                @Index(name = "idx_admin_audit_logs_deleted", columnList = "deleted"),
                @Index(name = "idx_admin_audit_logs_created_at", columnList = "created_at")
        }
)
public class AdminAuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 80)
    private AdminAuditAction action;

    @Column(name = "target_type", nullable = false, length = 80)
    private String targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
}