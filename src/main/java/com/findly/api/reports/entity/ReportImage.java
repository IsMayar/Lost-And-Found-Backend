package com.findly.api.reports.entity;

import com.findly.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "report_images",
        indexes = {
                @Index(name = "idx_report_images_report_id", columnList = "report_id"),
                @Index(name = "idx_report_images_deleted", columnList = "deleted"),
                @Index(name = "idx_report_images_sort_order", columnList = "sort_order")
        }
)
public class ReportImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 255)
    private String originalName;

    @Column(length = 120)
    private String contentType;

    private Long sizeBytes;

    @Column(nullable = false)
    private int sortOrder = 0;

    @Column(nullable = false)
    private boolean primaryImage = false;
}
