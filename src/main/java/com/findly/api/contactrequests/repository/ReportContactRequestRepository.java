package com.findly.api.contactrequests.repository;

import com.findly.api.contactrequests.entity.ReportContactRequest;
import com.findly.api.reports.entity.Report;
import com.findly.api.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportContactRequestRepository extends JpaRepository<ReportContactRequest, UUID> {

    Optional<ReportContactRequest> findByIdAndDeletedFalse(UUID id);

    Optional<ReportContactRequest> findByRequesterAndReportAndDeletedFalse(User requester, Report report);

    List<ReportContactRequest> findByRequesterAndDeletedFalseOrderByCreatedAtDesc(User requester);

    List<ReportContactRequest> findByOwnerAndDeletedFalseOrderByCreatedAtDesc(User owner);
}