package com.diya.dto.request;

import com.diya.model.Promotion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {

    @NotBlank(message = "Promotion name is required")
    private String name;

    @NotBlank(message = "Promotion code is required")
    private String code;

    private String description;

    @NotNull(message = "Promotion type is required")
    private Promotion.PromotionType type;

    @NotNull(message = "Promotion value is required")
    private BigDecimal value;

    private BigDecimal minimumOrderAmount;

    private BigDecimal maximumDiscountAmount;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotNull(message = "Usage limit is required")
    private Integer usageLimit;

    private boolean active = true;

    private Set<Long> applicableCategoryIds = new HashSet<>();

    private Set<Long> applicableProductIds = new HashSet<>();
}
