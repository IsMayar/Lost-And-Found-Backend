package com.findly.api.reports.repository;

import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.reports.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID>, JpaSpecificationExecutor<Report> {

    List<Report> findByOwnerIdAndDeletedFalseOrderByCreatedAtDesc(UUID ownerId);

    List<Report> findByTypeAndStatusAndDeletedFalseOrderByCreatedAtDesc(
            ReportType type,
            ReportStatus status
    );

    long countByDeletedFalse();

    long countByStatusAndDeletedFalse(ReportStatus status);

    long countByVerifiedAndDeletedFalse(boolean verified);
}