
package com.diya.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private int totalOrders;
    private double averageConversionRate;
    private Map<LocalDate, BigDecimal> revenueByDay = new HashMap<>();
}
