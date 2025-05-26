
package com.diya.mapper;

import com.diya.dto.response.AnalyticsResponse;
import com.diya.model.Analytics;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsMapper {

    public AnalyticsResponse toAnalyticsResponse(Analytics analytics) {
        return AnalyticsResponse.builder()
                .id(analytics.getId())
                .date(analytics.getDate())
                .totalVisitors(analytics.getTotalVisitors())
                .uniqueVisitors(analytics.getUniqueVisitors())
                .newUsers(analytics.getNewUsers())
                .pageViews(analytics.getPageViews())
                .orders(analytics.getOrders())
                .totalRevenue(analytics.getTotalRevenue())
                .conversionRate(analytics.getConversionRate())
                .bounceRate(analytics.getBounceRate())
                .averageSessionDuration(analytics.getAverageSessionDuration())
                .topSellingProducts(analytics.getTopSellingProducts())
                .topCategories(analytics.getTopCategories())
                .createdAt(analytics.getCreatedAt())
                .updatedAt(analytics.getUpdatedAt())
                .build();
    }
}
