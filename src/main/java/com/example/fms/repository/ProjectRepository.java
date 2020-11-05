package com.example.fms.repository;

import com.example.fms.entity.Department;
import com.example.fms.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByNameContaining(String name);
    List<Project> findAllByDateCreatedAfter(LocalDateTime after);
    List<Project> findAllByDateCreatedBefore(LocalDateTime before);
}
