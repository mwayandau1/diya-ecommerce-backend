
package com.diya.service;

import com.diya.dto.request.AnalyticsRequest;
import com.diya.dto.response.AnalyticsResponse;
import com.diya.dto.response.AnalyticsSummaryResponse;
import com.diya.dto.response.PagedResponse;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.AnalyticsMapper;
import com.diya.model.Analytics;
import com.diya.repository.AnalyticsRepository;
import com.diya.repository.OrderRepository;
import com.diya.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AnalyticsMapper analyticsMapper;

    public PagedResponse<AnalyticsResponse> getAllAnalytics(Pageable pageable) {
        Page<Analytics> analytics = analyticsRepository.findAllByOrderByDateDesc(pageable);
        return createPagedResponse(analytics);
    }

    public List<AnalyticsResponse> getAnalyticsByDateRange(LocalDate startDate, LocalDate endDate) {
        return analyticsRepository.findByDateBetween(startDate, endDate).stream()
                .map(analyticsMapper::toAnalyticsResponse)
                .collect(Collectors.toList());
    }

    public AnalyticsResponse getAnalyticsByDate(LocalDate date) {
        Analytics analytics = analyticsRepository.findByDate(date)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics not found for date: " + date));
        return analyticsMapper.toAnalyticsResponse(analytics);
    }

    public AnalyticsSummaryResponse getAnalyticsSummary(LocalDate startDate, LocalDate endDate) {
        Double totalRevenue = analyticsRepository.getTotalRevenueInRange(startDate, endDate);
        Integer totalOrders = analyticsRepository.getTotalOrdersInRange(startDate, endDate);
        Double averageConversionRate = analyticsRepository.getAverageConversionRateInRange(startDate, endDate);
        
        List<Object[]> revenueByDay = analyticsRepository.getRevenueByDay(startDate, endDate);
        Map<LocalDate, BigDecimal> revenueByDayMap = new HashMap<>();
        
        for (Object[] row : revenueByDay) {
            LocalDate day = (LocalDate) row[0];
            BigDecimal revenue = (BigDecimal) row[1];
            revenueByDayMap.put(day, revenue);
        }
        
        return AnalyticsSummaryResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue != null ? BigDecimal.valueOf(totalRevenue) : BigDecimal.ZERO)
                .totalOrders(totalOrders != null ? totalOrders : 0)
                .averageConversionRate(averageConversionRate != null ? averageConversionRate : 0.0)
                .revenueByDay(revenueByDayMap)
                .build();
    }

    @Transactional
    public AnalyticsResponse recordDailyAnalytics(AnalyticsRequest request) {
        LocalDate today = LocalDate.now();
        
        // Check if analytics already exist for today
        Analytics analytics = analyticsRepository.findByDate(today)
                .orElse(new Analytics());
        
        analytics.setDate(today);
        analytics.setTotalVisitors(request.getTotalVisitors());
        analytics.setUniqueVisitors(request.getUniqueVisitors());
        analytics.setNewUsers(request.getNewUsers());
        analytics.setPageViews(request.getPageViews());
        
        // Calculate orders and revenue from order data
        LocalDateTime startOfDay = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(today, LocalTime.MAX);
        
        Long orderCount = orderRepository.countOrdersInPeriod(startOfDay, endOfDay);
        Double revenue = orderRepository.getRevenueInPeriod(startOfDay, endOfDay);
        
        analytics.setOrders(orderCount != null ? orderCount.intValue() : 0);
        analytics.setTotalRevenue(revenue != null ? BigDecimal.valueOf(revenue) : BigDecimal.ZERO);
        
        // Calculate conversion rate
        if (analytics.getUniqueVisitors() > 0) {
            double conversionRate = (double) analytics.getOrders() / analytics.getUniqueVisitors() * 100;
            analytics.setConversionRate(conversionRate);
        } else {
            analytics.setConversionRate(0.0);
        }
        
        // Set bounce rate and session duration from request
        analytics.setBounceRate(request.getBounceRate());
        analytics.setAverageSessionDuration(request.getAverageSessionDuration());
        
        // Get top selling products
        List<String> topProducts = productRepository.findBestSellingProducts(5).stream()
                .map(p -> p.getName() + " (ID: " + p.getId() + ")")
                .collect(Collectors.toList());
        
        analytics.setTopSellingProducts(String.join(", ", topProducts));
        
        Analytics savedAnalytics = analyticsRepository.save(analytics);
        return analyticsMapper.toAnalyticsResponse(savedAnalytics);
    }

    private PagedResponse<AnalyticsResponse> createPagedResponse(Page<Analytics> analytics) {
        List<AnalyticsResponse> responses = analytics.getContent()
                .stream()
                .map(analyticsMapper::toAnalyticsResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                analytics.getNumber(),
                analytics.getSize(),
                analytics.getTotalElements(),
                analytics.getTotalPages(),
                analytics.isLast()
        );
    }
}
