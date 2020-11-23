package com.example.fms.service;

import com.example.fms.dto.ProjectDTO;
import com.example.fms.entity.Project;
import com.example.fms.entity.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProjectService {

    List<Project> getAll(boolean isDeleted);
    Page<Project> getByPage(List<Project> list, Pageable pageable);
    List<Project> getAllByNameContaining(String name);
    List<Project> getAllByDateCreatedBefore(String before);
    List<Project> getAllByDateCreatedAfter(String after);
    ResponseEntity<Project> addProject(ProjectDTO projectDTO, String userEmail);
    ResponseEntity<Project> getProjectById(Long id);
    ResponseEntity<Project> updateProjectById(ProjectDTO projectDTO, Long id, String userEmail);
    ResponseMessage deleteProjectById(Long id, String userEmail);

}
