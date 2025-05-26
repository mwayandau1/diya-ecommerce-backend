package com.diya.dto.response;

import com.diya.model.Promotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Promotion.PromotionType type;
    private BigDecimal value;
    private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Integer usageCount;
    private boolean active;
    private List<CategoryResponse> applicableCategories = new ArrayList<>();
    private List<ProductResponse> applicableProducts = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}