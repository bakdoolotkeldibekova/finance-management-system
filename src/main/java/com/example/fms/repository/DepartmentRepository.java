package com.example.fms.repository;

import com.example.fms.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findAllByOrderByDateCreatedDesc();
    List<Department> findAllByNameContainingIgnoringCase(String name);
    List<Department> findAllByDateCreatedAfter(LocalDateTime after);
    List<Department> findAllByDateCreatedBefore(LocalDateTime before);
}
