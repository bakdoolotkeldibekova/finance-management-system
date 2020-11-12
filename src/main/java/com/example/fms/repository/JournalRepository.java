package com.example.fms.repository;

import com.example.fms.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findAllByDeleted (boolean isDeleted);
    List<Journal> findAllByTableContainingIgnoringCase(String table);
    List<Journal> findAllByActionContainingIgnoringCase(String action);
    List<Journal> findAllByUserId(Long userId);

    List<Journal> findAllByDateCreatedAfter(LocalDateTime after);
    List<Journal> findAllByDateCreatedBefore(LocalDateTime before);
}
