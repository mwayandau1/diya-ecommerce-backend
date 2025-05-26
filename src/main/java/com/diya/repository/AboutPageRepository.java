
package com.diya.repository;

import com.diya.model.AboutPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AboutPageRepository extends JpaRepository<AboutPage, Long> {
    Optional<AboutPage> findByActiveTrue();
}
