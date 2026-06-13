package com.findly.api.reports.entity;

import com.findly.api.common.entity.BaseEntity;
import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.enums.ReportStatus;
import com.findly.api.common.enums.ReportType;
import com.findly.api.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "reports",
        indexes = {
                @Index(name = "idx_reports_owner_id", columnList = "owner_id"),
                @Index(name = "idx_reports_type", columnList = "type"),
                @Index(name = "idx_reports_category", columnList = "category"),
                @Index(name = "idx_reports_status", columnList = "status"),
                @Index(name = "idx_reports_city", columnList = "city"),
                @Index(name = "idx_reports_deleted", columnList = "deleted")
        }
)
public class Report extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ReportCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ReportStatus status = ReportStatus.ACTIVE;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String locationText;

    @Column(length = 120)
    private String city;

    @Column(length = 120)
    private String country;

    private LocalDate eventDate;

    @Column(length = 80)
    private String color;

    @Column(length = 120)
    private String brand;

    @Column(columnDefinition = "TEXT")
    private String privateHint;

    @Column(length = 120)
    private String contactName;

    @Column(length = 40)
    private String contactPhone;

    @Column(length = 180)
    private String contactEmail;

    @Column(nullable = false)
    private boolean verified = false;
}
