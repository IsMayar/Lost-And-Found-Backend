package com.findly.api.categories.service;

import com.findly.api.categories.dto.CategoryConfigResponse;
import com.findly.api.categories.entity.CategoryConfig;
import com.findly.api.categories.repository.CategoryConfigRepository;
import com.findly.api.common.enums.ReportCategory;
import com.findly.api.common.exception.ApiException;
import com.findly.api.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryConfigService {

    private final CategoryConfigRepository categoryConfigRepository;

    @Transactional(readOnly = true)
    public List<CategoryConfigResponse> getActiveCategories(String keyword) {
        return categoryConfigRepository.findByActiveTrueAndDeletedFalseOrderBySortOrderAscCreatedAtDesc()
                .stream()
                .filter(categoryConfig -> matchesKeyword(categoryConfig, keyword))
                .sorted(Comparator.comparingInt(CategoryConfig::getSortOrder))
                .map(CategoryConfigResponse::fromCategoryConfig)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryConfigResponse getActiveCategoryByCategory(ReportCategory category) {
        CategoryConfig categoryConfig = categoryConfigRepository.findByCategoryAndDeletedFalse(category)
                .filter(CategoryConfig::isActive)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Category not found"));

        return CategoryConfigResponse.fromCategoryConfig(categoryConfig);
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