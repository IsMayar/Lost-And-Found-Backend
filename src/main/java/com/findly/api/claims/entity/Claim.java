package com.findly.api.claims.entity;

import com.findly.api.common.entity.BaseEntity;
import com.findly.api.common.enums.ClaimStatus;
import com.findly.api.reports.entity.Report;
import com.findly.api.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "claims",
        indexes = {
                @Index(name = "idx_claims_report_id", columnList = "report_id"),
                @Index(name = "idx_claims_claimant_id", columnList = "claimant_id"),
                @Index(name = "idx_claims_status", columnList = "status"),
                @Index(name = "idx_claims_deleted", columnList = "deleted")
        }
)
public class Claim extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String proofText;
}
