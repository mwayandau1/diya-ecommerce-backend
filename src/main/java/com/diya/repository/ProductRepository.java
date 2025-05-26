
package com.diya.repository;

import com.diya.model.Category;
import com.diya.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    
    Page<Product> findByCategory(Category category, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.featured = true")
    List<Product> findAllFeaturedProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> search(String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stock < 10")
    List<Product> findLowStockProducts();
    
    @Query(value = "SELECT p.* FROM products p INNER JOIN order_items oi ON p.id = oi.product_id " +
           "GROUP BY p.id ORDER BY SUM(oi.quantity) DESC LIMIT :limit", nativeQuery = true)
    List<Product> findBestSellingProducts(int limit);
}
