
package com.diya.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "analytics")
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int totalVisitors;

    @Column(nullable = false)
    private int uniqueVisitors;

    @Column(nullable = false)
    private int newUsers;

    @Column(nullable = false)
    private int pageViews;

    @Column(nullable = false)
    private int orders;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private double conversionRate;

    @Column(nullable = false)
    private double bounceRate;

    @Column(nullable = false)
    private double averageSessionDuration;

    @Column
    private String topSellingProducts;

    @Column
    private String topCategories;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
