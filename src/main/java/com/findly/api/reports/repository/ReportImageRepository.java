package com.findly.api.reports.repository;

import com.findly.api.reports.entity.ReportImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportImageRepository extends JpaRepository<ReportImage, UUID> {

    List<ReportImage> findByReportIdAndDeletedFalseOrderBySortOrderAscCreatedAtAsc(UUID reportId);

    Optional<ReportImage> findByIdAndReportIdAndDeletedFalse(UUID id, UUID reportId);

    long countByReportIdAndDeletedFalse(UUID reportId);
}
