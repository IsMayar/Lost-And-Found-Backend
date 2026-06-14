package com.findly.api.categories.entity;

import com.findly.api.common.entity.BaseEntity;
import com.findly.api.common.enums.ReportCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "category_configs",
        indexes = {
                @Index(name = "idx_category_configs_category", columnList = "category"),
                @Index(name = "idx_category_configs_active", columnList = "active"),
                @Index(name = "idx_category_configs_deleted", columnList = "deleted"),
                @Index(name = "idx_category_configs_sort_order", columnList = "sort_order")
        }
)
public class CategoryConfig extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 80)
    private ReportCategory category;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_name", length = 80)
    private String iconName;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}