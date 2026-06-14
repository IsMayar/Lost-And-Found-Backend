package com.findly.api.admin.service;

import com.findly.api.admin.dto.AdminCategoryConfigResponse;
import com.findly.api.admin.dto.AdminCreateCategoryConfigRequest;
import com.findly.api.admin.dto.AdminUpdateCategoryConfigRequest;
import com.findly.api.categories.entity.CategoryConfig;
import com.findly.api.categories.repository.CategoryConfigRepository;
import com.findly.api.common.enums.AdminAuditAction;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCategoryConfigService {

    private final CategoryConfigRepository categoryConfigRepository;
    private final AdminAuditLogService adminAuditLogService;

    @Transactional(readOnly = true)
    public List<AdminCategoryConfigResponse> getCategoryConfigs(Boolean active, String keyword) {
        return categoryConfigRepository.findByDeletedFalseOrderBySortOrderAscCreatedAtDesc()
                .stream()
                .filter(categoryConfig -> active == null || categoryConfig.isActive() == active)
                .filter(categoryConfig -> matchesKeyword(categoryConfig, keyword))
                .sorted(Comparator.comparingInt(CategoryConfig::getSortOrder))
                .map(AdminCategoryConfigResponse::fromCategoryConfig)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminCategoryConfigResponse getCategoryConfigById(UUID id) {
        CategoryConfig categoryConfig = categoryConfigRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Category config not found"));

        return AdminCategoryConfigResponse.fromCategoryConfig(categoryConfig);
    }

    @Transactional
    public AdminCategoryConfigResponse createCategoryConfig(AdminCreateCategoryConfigRequest request) {
        CategoryConfig categoryConfig = categoryConfigRepository
                .findByCategoryAndDeletedFalse(request.category())
                .orElseGet(CategoryConfig::new);

        boolean creatingNew = categoryConfig.getId() == null;

        categoryConfig.setCategory(request.category());
        categoryConfig.setDisplayName(request.displayName().trim());
        categoryConfig.setDescription(cleanNullable(request.description()));
        categoryConfig.setIconName(cleanNullable(request.iconName()));
        categoryConfig.setActive(request.active() == null || request.active());
        categoryConfig.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());

        CategoryConfig savedCategoryConfig = categoryConfigRepository.save(categoryConfig);

        adminAuditLogService.log(
                creatingNew ? AdminAuditAction.CATEGORY_CONFIG_CREATED : AdminAuditAction.CATEGORY_CONFIG_UPDATED,
                "CATEGORY_CONFIG",
                savedCategoryConfig.getId(),
                (creatingNew ? "Created" : "Updated")
                        + " category config for "
                        + savedCategoryConfig.getCategory()
        );

        return AdminCategoryConfigResponse.fromCategoryConfig(savedCategoryConfig);
    }

    @Transactional
    public AdminCategoryConfigResponse updateCategoryConfig(UUID id, AdminUpdateCategoryConfigRequest request) {
        CategoryConfig categoryConfig = categoryConfigRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Category config not found"));

        if (request.displayName() != null) {
            categoryConfig.setDisplayName(request.displayName().trim());
        }

        if (request.description() != null) {
            categoryConfig.setDescription(cleanNullable(request.description()));
        }

        if (request.iconName() != null) {
            categoryConfig.setIconName(cleanNullable(request.iconName()));
        }

        if (request.active() != null) {
            categoryConfig.setActive(request.active());
        }

        if (request.sortOrder() != null) {
            categoryConfig.setSortOrder(request.sortOrder());
        }

        CategoryConfig savedCategoryConfig = categoryConfigRepository.save(categoryConfig);

        adminAuditLogService.log(
                AdminAuditAction.CATEGORY_CONFIG_UPDATED,
                "CATEGORY_CONFIG",
                savedCategoryConfig.getId(),
                "Updated category config for " + savedCategoryConfig.getCategory()
        );

        return AdminCategoryConfigResponse.fromCategoryConfig(savedCategoryConfig);
    }

    @Transactional
    public void deleteCategoryConfig(UUID id) {
        CategoryConfig categoryConfig = categoryConfigRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Category config not found"));

        categoryConfig.markDeleted();
        categoryConfigRepository.save(categoryConfig);

        adminAuditLogService.log(
                AdminAuditAction.CATEGORY_CONFIG_DELETED,
                "CATEGORY_CONFIG",
                categoryConfig.getId(),
                "Deleted category config for " + categoryConfig.getCategory()
        );
    }

    private boolean matchesKeyword(CategoryConfig categoryConfig, String keyword) {
        String cleanedKeyword = cleanNullable(keyword);

        if (cleanedKeyword == null) {
            return true;
        }

        String pattern = cleanedKeyword.toLowerCase();

        return contains(categoryConfig.getCategory().name(), pattern)
                || contains(categoryConfig.getDisplayName(), pattern)
                || contains(categoryConfig.getDescription(), pattern)
                || contains(categoryConfig.getIconName(), pattern);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }
}