package com.example.fms.repository;

import com.example.fms.entity.Journal;
import com.example.fms.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findAllByNameContainingIgnoringCase(String name);

    List<Staff> findAllByDateCreatedAfter(LocalDateTime after);
    List<Staff> findAllByDateCreatedBefore(LocalDateTime before);
}
