package com.findly.api.categories.repository;

import com.findly.api.categories.entity.CategoryConfig;
import com.findly.api.common.enums.ReportCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryConfigRepository extends JpaRepository<CategoryConfig, UUID> {

    Optional<CategoryConfig> findByIdAndDeletedFalse(UUID id);

    Optional<CategoryConfig> findByCategoryAndDeletedFalse(ReportCategory category);

    List<CategoryConfig> findByDeletedFalseOrderBySortOrderAscCreatedAtDesc();

    List<CategoryConfig> findByActiveTrueAndDeletedFalseOrderBySortOrderAscCreatedAtDesc();
}