
package com.diya.repository;


import com.diya.model.BlogPost;
import com.diya.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Optional<BlogPost> findBySlug(String slug);
    
    Page<BlogPost> findByAuthor(User author, Pageable pageable);
    
    Page<BlogPost> findByPublishedTrue(Pageable pageable);
    
    @Query("SELECT b FROM BlogPost b WHERE b.published = true AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<BlogPost> search(String keyword, Pageable pageable);
    
    @Query("SELECT b FROM BlogPost b WHERE b.published = true AND :tag MEMBER OF b.tags")
    Page<BlogPost> findByTag(String tag, Pageable pageable);
    
    @Query("SELECT DISTINCT t FROM BlogPost b JOIN b.tags t WHERE b.published = true")
    List<String> findAllTags();
}
