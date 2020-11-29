package com.example.fms.repository;

import com.example.fms.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByDateCreatedDesc();
    List<Category> findAllByNameContainingIgnoringCase(String name);
    List<Category> findAllByDateCreatedAfter(LocalDateTime after);
    List<Category> findAllByDateCreatedBefore(LocalDateTime before);
}
