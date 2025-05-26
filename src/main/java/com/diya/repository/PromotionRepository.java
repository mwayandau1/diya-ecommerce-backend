package com.diya.repository;

import com.diya.model.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);

    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.startDate <= :now AND p.endDate >= :now AND p.usageCount < p.usageLimit")
    List<Promotion> findActivePromotions(LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.code = :code AND p.startDate <= :now AND p.endDate >= :now AND p.usageCount < p.usageLimit")
    Optional<Promotion> findValidPromotionByCode(String code, LocalDateTime now);

    Page<Promotion> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.endDate < :now")
    List<Promotion> findExpiredPromotions(LocalDateTime now);
}
