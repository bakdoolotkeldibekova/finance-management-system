package com.example.fms.service;

import com.example.fms.dto.ProjectDTO;
import com.example.fms.entity.Project;

import java.util.List;

public interface ProjectService {

    List<Project> getAll();
    List<Project> getAllByNameContaining(String name);
    List<Project> getAllByDateCreatedBefore(String before);
    List<Project> getAllByDateCreatedAfter(String after);
    Project addProject(ProjectDTO projectDTO, String userEmail);
    Project getProjectById(Long id);
    Project updateProjectById(ProjectDTO projectDTO, Long id, String userEmail);
    Project deleteProjectById(Long id, String userEmail);

}
