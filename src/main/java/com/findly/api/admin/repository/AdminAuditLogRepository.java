package com.findly.api.admin.repository;

import com.findly.api.admin.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, UUID> {

    List<AdminAuditLog> findByDeletedFalseOrderByCreatedAtDesc();

    Optional<AdminAuditLog> findByIdAndDeletedFalse(UUID id);
}