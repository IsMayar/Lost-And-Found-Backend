package com.findly.api.savedreports.entity;

import com.findly.api.common.entity.BaseEntity;
import com.findly.api.reports.entity.Report;
import com.findly.api.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "saved_reports",
        indexes = {
                @Index(name = "idx_saved_reports_user_id", columnList = "user_id"),
                @Index(name = "idx_saved_reports_report_id", columnList = "report_id"),
                @Index(name = "idx_saved_reports_deleted", columnList = "deleted"),
                @Index(name = "idx_saved_reports_created_at", columnList = "created_at")
        }
)
public class SavedReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
}