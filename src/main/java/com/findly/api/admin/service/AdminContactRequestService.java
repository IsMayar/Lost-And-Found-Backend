package com.findly.api.admin.service;

import com.findly.api.admin.dto.AdminContactRequestResponse;
import com.findly.api.admin.dto.AdminUpdateContactRequestStatusRequest;
import com.findly.api.common.enums.AdminAuditAction;
import com.findly.api.common.enums.ContactRequestStatus;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.contactrequests.entity.ReportContactRequest;
import com.findly.api.contactrequests.repository.ReportContactRequestRepository;
import com.findly.api.security.user.UserPrincipal;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminContactRequestService {

    private static final String TARGET_TYPE = "CONTACT_REQUEST";

    private final ReportContactRequestRepository contactRequestRepository;
    private final AdminAuditLogService adminAuditLogService;

    @Transactional(readOnly = true)
    public Page<AdminContactRequestResponse> getContactRequests(
            ContactRequestStatus status,
            UUID reportId,
            UUID requesterId,
            UUID ownerId,
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<ReportContactRequest> specification = notDeleted()
                .and(statusEquals(status))
                .and(reportIdEquals(reportId))
                .and(requesterIdEquals(requesterId))
                .and(ownerIdEquals(ownerId))
                .and(keywordContains(keyword));

        return contactRequestRepository.findAll(specification, pageable)
                .map(AdminContactRequestResponse::fromContactRequest);
    }

    @Transactional(readOnly = true)
    public AdminContactRequestResponse getContactRequest(UUID id) {
        ReportContactRequest contactRequest = contactRequestRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Contact request not found"));

        return AdminContactRequestResponse.fromContactRequest(contactRequest);
    }

    @Transactional
    public AdminContactRequestResponse updateStatus(
            UUID id,
            AdminUpdateContactRequestStatusRequest request,
            UserPrincipal admin
    ) {
        if (admin == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Authentication is required");
        }

        ReportContactRequest contactRequest = contactRequestRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Contact request not found"));

        ContactRequestStatus oldStatus = contactRequest.getStatus();

        contactRequest.setStatus(request.status());
        contactRequest.setResponseMessage(cleanNullable(request.responseMessage()));
        contactRequest.setRespondedAt(Instant.now());

        ReportContactRequest saved = contactRequestRepository.save(contactRequest);

        adminAuditLogService.log(
                AdminAuditAction.CONTACT_REQUEST_STATUS_UPDATED,
                TARGET_TYPE,
                saved.getId(),
                "Contact request status updated from " + oldStatus + " to " + saved.getStatus()
        );

        return AdminContactRequestResponse.fromContactRequest(saved);
    }

    private Specification<ReportContactRequest> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    private Specification<ReportContactRequest> statusEquals(ContactRequestStatus status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    private Specification<ReportContactRequest> reportIdEquals(UUID reportId) {
        return (root, query, cb) -> {
            if (reportId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("report").get("id"), reportId);
        };
    }

    private Specification<ReportContactRequest> requesterIdEquals(UUID requesterId) {
        return (root, query, cb) -> {
            if (requesterId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("requester").get("id"), requesterId);
        };
    }

    private Specification<ReportContactRequest> ownerIdEquals(UUID ownerId) {
        return (root, query, cb) -> {
            if (ownerId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("owner").get("id"), ownerId);
        };
    }

    private Specification<ReportContactRequest> keywordContains(String keyword) {
        return (root, query, cb) -> {
            String cleanedKeyword = cleanNullable(keyword);

            if (cleanedKeyword == null) {
                return cb.conjunction();
            }

            String pattern = "%" + cleanedKeyword.toLowerCase() + "%";

            var report = root.join("report", JoinType.LEFT);
            var requester = root.join("requester", JoinType.LEFT);
            var owner = root.join("owner", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("message")), pattern),
                    cb.like(cb.lower(root.get("responseMessage")), pattern),
                    cb.like(cb.lower(report.get("title")), pattern),
                    cb.like(cb.lower(requester.get("fullName")), pattern),
                    cb.like(cb.lower(requester.get("email")), pattern),
                    cb.like(cb.lower(owner.get("fullName")), pattern),
                    cb.like(cb.lower(owner.get("email")), pattern)
            );
        };
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }
}