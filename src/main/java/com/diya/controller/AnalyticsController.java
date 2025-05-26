//
//package com.diya.controller;
//
//import com.diya.dto.response.AnalyticsResponse;
//import com.diya.dto.response.AnalyticsSummaryResponse;
//import com.diya.service.AnalyticsService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//
//@RestController
//@RequestMapping("/api/v1/admin/analytics")
//@PreAuthorize("hasRole('ADMIN')")
//@RequiredArgsConstructor
//public class AnalyticsController {
//
//    private final AnalyticsService analyticsService;
//
//    @GetMapping("/summary")
//    public ResponseEntity<AnalyticsSummaryResponse> getAnalyticsSummary() {
//        return ResponseEntity.ok(analyticsService.getAnalyticsSummary());
//    }
//
//    @GetMapping("/sales")
//    public ResponseEntity<AnalyticsResponse> getSalesAnalytics(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//        return ResponseEntity.ok(analyticsService.getSalesAnalytics(startDate, endDate));
//    }
//
//    @GetMapping("/products")
//    public ResponseEntity<AnalyticsResponse> getProductAnalytics(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//        return ResponseEntity.ok(analyticsService.getProductAnalytics(startDate, endDate));
//    }
//
//    @GetMapping("/customers")
//    public ResponseEntity<AnalyticsResponse> getCustomerAnalytics(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//        return ResponseEntity.ok(analyticsService.getCustomerAnalytics(startDate, endDate));
//    }
//
//    @PostMapping("/record")
//    public ResponseEntity<Void> recordAnalyticsEvent(@Valid @RequestBody AnalyticsRequest analyticsRequest) {
//        analyticsService.recordEvent(analyticsRequest);
//        return ResponseEntity.ok().build();
//    }
//}
