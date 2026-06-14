package com.findly.api.contactrequests.entity;

import com.findly.api.common.entity.BaseEntity;
import com.findly.api.common.enums.ContactRequestStatus;
import com.findly.api.reports.entity.Report;
import com.findly.api.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "report_contact_requests",
        indexes = {
                @Index(name = "idx_report_contact_requests_report_id", columnList = "report_id"),
                @Index(name = "idx_report_contact_requests_requester_id", columnList = "requester_id"),
                @Index(name = "idx_report_contact_requests_owner_id", columnList = "owner_id"),
                @Index(name = "idx_report_contact_requests_status", columnList = "status"),
                @Index(name = "idx_report_contact_requests_deleted", columnList = "deleted"),
                @Index(name = "idx_report_contact_requests_created_at", columnList = "created_at")
        }
)
public class ReportContactRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private ContactRequestStatus status = ContactRequestStatus.PENDING;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    @Column(name = "responded_at")
    private Instant respondedAt;
}