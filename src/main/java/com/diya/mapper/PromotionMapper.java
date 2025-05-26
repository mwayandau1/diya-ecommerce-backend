
package com.diya.mapper;
import com.diya.dto.request.PromotionRequest;
import com.diya.dto.response.PromotionResponse;
import com.diya.model.Promotion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PromotionMapper {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public PromotionResponse toPromotionResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .type(promotion.getType())
                .value(promotion.getValue())
                .minimumOrderAmount(promotion.getMinimumOrderAmount())
                .maximumDiscountAmount(promotion.getMaximumDiscountAmount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .usageLimit(promotion.getUsageLimit())
                .usageCount(promotion.getUsageCount())
                .active(promotion.isActive())
                .applicableCategories(promotion.getApplicableCategories().stream()
                        .map(categoryMapper::toCategoryResponse)
                        .collect(Collectors.toList()))
                .applicableProducts(promotion.getApplicableProducts().stream()
                        .map(productMapper::toProductResponse)
                        .collect(Collectors.toList()))
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .build();
    }

    public Promotion toPromotion(PromotionRequest request) {
        return Promotion.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .type(request.getType())
                .value(request.getValue())
                .minimumOrderAmount(request.getMinimumOrderAmount())
                .maximumDiscountAmount(request.getMaximumDiscountAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .active(request.isActive())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updatePromotionFromRequest(PromotionRequest request, Promotion promotion) {
        promotion.setName(request.getName());
        promotion.setCode(request.getCode());
        promotion.setDescription(request.getDescription());
        promotion.setType(request.getType());
        promotion.setValue(request.getValue());
        promotion.setMinimumOrderAmount(request.getMinimumOrderAmount());
        promotion.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setActive(request.isActive());
        promotion.setUpdatedAt(LocalDateTime.now());
    }
}