package com.findly.api.contactrequests.service;

import com.findly.api.common.enums.ContactRequestStatus;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import com.findly.api.contactrequests.dto.CreateContactRequestRequest;
import com.findly.api.contactrequests.dto.ReportContactRequestResponse;
import com.findly.api.contactrequests.dto.UpdateContactRequestStatusRequest;
import com.findly.api.contactrequests.entity.ReportContactRequest;
import com.findly.api.contactrequests.repository.ReportContactRequestRepository;
import com.findly.api.reports.entity.Report;
import com.findly.api.reports.repository.ReportRepository;
import com.findly.api.security.user.UserPrincipal;
import com.findly.api.users.entity.User;
import com.findly.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportContactRequestService {

    private final ReportContactRequestRepository contactRequestRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportContactRequestResponse createContactRequest(
            UUID reportId,
            CreateContactRequestRequest request,
            UserPrincipal principal
    ) {
        User requester = getCurrentUser(principal);
        Report report = getReport(reportId);
        User owner = report.getOwner();

        if (owner == null || owner.isDeleted()) {
            throw new ApiException(ErrorCode.NOT_FOUND, "Report owner not found");
        }

        if (owner.getId().equals(requester.getId())) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "You cannot request contact for your own report");
        }

        return contactRequestRepository.findByRequesterAndReportAndDeletedFalse(requester, report)
                .map(ReportContactRequestResponse::fromContactRequest)
                .orElseGet(() -> {
                    ReportContactRequest contactRequest = new ReportContactRequest();
                    contactRequest.setRequester(requester);
                    contactRequest.setOwner(owner);
                    contactRequest.setReport(report);
                    contactRequest.setStatus(ContactRequestStatus.PENDING);
                    contactRequest.setMessage(request == null ? null : cleanNullable(request.message()));

                    ReportContactRequest saved = contactRequestRepository.save(contactRequest);
                    return ReportContactRequestResponse.fromContactRequest(saved);
                });
    }

    @Transactional(readOnly = true)
    public List<ReportContactRequestResponse> getMySentRequests(UserPrincipal principal) {
        User requester = getCurrentUser(principal);

        return contactRequestRepository.findByRequesterAndDeletedFalseOrderByCreatedAtDesc(requester)
                .stream()
                .filter(contactRequest -> contactRequest.getReport() != null && !contactRequest.getReport().isDeleted())
                .map(ReportContactRequestResponse::fromContactRequest)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportContactRequestResponse> getMyReceivedRequests(UserPrincipal principal) {
        User owner = getCurrentUser(principal);

        return contactRequestRepository.findByOwnerAndDeletedFalseOrderByCreatedAtDesc(owner)
                .stream()
                .filter(contactRequest -> contactRequest.getReport() != null && !contactRequest.getReport().isDeleted())
                .map(ReportContactRequestResponse::fromContactRequest)
                .toList();
    }

    @Transactional
    public ReportContactRequestResponse updateStatus(
            UUID contactRequestId,
            UpdateContactRequestStatusRequest request,
            UserPrincipal principal
    ) {
        User owner = getCurrentUser(principal);

        ReportContactRequest contactRequest = contactRequestRepository.findByIdAndDeletedFalse(contactRequestId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Contact request not found"));

        if (!contactRequest.getOwner().getId().equals(owner.getId())) {
            throw new ApiException(ErrorCode.NOT_FOUND, "Contact request not found");
        }

        if (request.status() == ContactRequestStatus.PENDING) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Status must be APPROVED or REJECTED");
        }

        contactRequest.setStatus(request.status());
        contactRequest.setResponseMessage(cleanNullable(request.responseMessage()));
        contactRequest.setRespondedAt(Instant.now());

        ReportContactRequest saved = contactRequestRepository.save(contactRequest);
        return ReportContactRequestResponse.fromContactRequest(saved);
    }

    private User getCurrentUser(UserPrincipal principal) {
        if (principal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Authentication is required");
        }

        return userRepository.findById(principal.getId())
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "User not found"));
    }

    private Report getReport(UUID reportId) {
        return reportRepository.findById(reportId)
                .filter(report -> !report.isDeleted())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Report not found"));
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }
}