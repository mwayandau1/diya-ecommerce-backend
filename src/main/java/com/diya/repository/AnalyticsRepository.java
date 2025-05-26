
package com.diya.repository;

import com.diya.model.Analytics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    Optional<Analytics> findByDate(LocalDate date);
    
    List<Analytics> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(a.totalRevenue) FROM Analytics a WHERE a.date BETWEEN :startDate AND :endDate")
    Double getTotalRevenueInRange(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(a.orders) FROM Analytics a WHERE a.date BETWEEN :startDate AND :endDate")
    Integer getTotalOrdersInRange(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT AVG(a.conversionRate) FROM Analytics a WHERE a.date BETWEEN :startDate AND :endDate")
    Double getAverageConversionRateInRange(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT a.date, a.totalRevenue FROM Analytics a WHERE a.date BETWEEN :startDate AND :endDate ORDER BY a.date")
    List<Object[]> getRevenueByDay(LocalDate startDate, LocalDate endDate);
    
    Page<Analytics> findAllByOrderByDateDesc(Pageable pageable);
}
