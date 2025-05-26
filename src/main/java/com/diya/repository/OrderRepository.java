
package com.diya.repository;

import com.diya.model.Order;
import com.diya.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByUser(User user, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Long countOrdersInPeriod(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED'")
    Double getTotalRevenue();
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    Double getRevenueInPeriod(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT MONTH(o.createdAt), YEAR(o.createdAt), COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate GROUP BY MONTH(o.createdAt), YEAR(o.createdAt)")
    List<Object[]> getOrderCountByMonth(LocalDateTime startDate, LocalDateTime endDate);
}
