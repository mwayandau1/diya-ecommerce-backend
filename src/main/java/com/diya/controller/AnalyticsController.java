package com.diya.controller;

import com.diya.dto.request.AnalyticsRequest;
import com.diya.dto.response.AnalyticsResponse;
import com.diya.dto.response.AnalyticsSummaryResponse;
import com.diya.dto.response.PagedResponse;
import com.diya.service.AnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<PagedResponse<AnalyticsResponse>> getAllAnalytics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        PagedResponse<AnalyticsResponse> analytics = analyticsService.getAllAnalytics(pageable);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AnalyticsResponse>> getAnalyticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<AnalyticsResponse> analytics = analyticsService.getAnalyticsByDateRange(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<AnalyticsResponse> getAnalyticsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        AnalyticsResponse analytics = analyticsService.getAnalyticsByDate(date);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryResponse> getAnalyticsSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AnalyticsSummaryResponse summary = analyticsService.getAnalyticsSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/record")
    public ResponseEntity<AnalyticsResponse> recordDailyAnalytics(@Valid @RequestBody AnalyticsRequest analyticsRequest) {
        AnalyticsResponse analytics = analyticsService.recordDailyAnalytics(analyticsRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(analytics);
    }
}