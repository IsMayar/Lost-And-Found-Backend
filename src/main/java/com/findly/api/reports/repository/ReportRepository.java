package com.findly.api.reports.repository;

import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.reports.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    List<Report> findByOwnerIdAndDeletedFalseOrderByCreatedAtDesc(UUID ownerId);

    List<Report> findByTypeAndStatusAndDeletedFalseOrderByCreatedAtDesc(
            ReportType type,
            ReportStatus status
    );
}
