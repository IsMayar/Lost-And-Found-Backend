package com.findly.api.savedreports.repository;

import com.findly.api.reports.entity.Report;
import com.findly.api.savedreports.entity.SavedReport;
import com.findly.api.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedReportRepository extends JpaRepository<SavedReport, UUID> {

    Optional<SavedReport> findByUserAndReportAndDeletedFalse(User user, Report report);

    boolean existsByUserAndReportAndDeletedFalse(User user, Report report);

    List<SavedReport> findByUserAndDeletedFalseOrderByCreatedAtDesc(User user);
}