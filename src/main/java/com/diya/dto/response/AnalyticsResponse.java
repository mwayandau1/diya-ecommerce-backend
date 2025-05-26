
package com.diya.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private Long id;
    private LocalDate date;
    private int totalVisitors;
    private int uniqueVisitors;
    private int newUsers;
    private int pageViews;
    private int orders;
    private BigDecimal totalRevenue;
    private double conversionRate;
    private double bounceRate;
    private double averageSessionDuration;
    private String topSellingProducts;
    private String topCategories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
