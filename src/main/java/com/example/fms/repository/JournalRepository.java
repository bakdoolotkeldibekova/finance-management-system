package com.example.fms.repository;

import com.example.fms.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findAllByAction1ContainingIgnoringCase(String action);
    List<Journal> findAllByAction2ContainingIgnoringCase(String action);
    List<Journal> findAllByUserId(Long userId);
}
