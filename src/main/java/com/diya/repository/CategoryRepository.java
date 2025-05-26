
package com.diya.repository;

import com.diya.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    
    List<Category> findByParentIsNull();
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId")
    List<Category> findSubcategories(Long parentId);
    
    boolean existsByName(String name);
}
