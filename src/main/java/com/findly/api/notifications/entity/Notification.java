package com.findly.api.notifications.entity;

import com.findly.api.claims.entity.Claim;
import com.findly.api.common.entity.BaseEntity;
import com.findly.api.common.enums.NotificationType;
import com.findly.api.reports.entity.Report;
import com.findly.api.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user_id", columnList = "user_id"),
                @Index(name = "idx_notifications_type", columnList = "type"),
                @Index(name = "idx_notifications_read", columnList = "read"),
                @Index(name = "idx_notifications_deleted", columnList = "deleted")
        }
)
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private boolean read = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id")
    private Claim claim;
}
