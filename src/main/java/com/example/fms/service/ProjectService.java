package com.example.fms.service;

import com.example.fms.entity.Department;
import com.example.fms.entity.Project;
import com.example.fms.entity.User;

import java.util.List;

public interface ProjectService {

    List<Project> getAll();
    List<Project> getAllByNameContaining(String name);
    List<Project> getAllByDateCreatedBefore(String before);
    List<Project> getAllByDateCreatedAfter(String after);
    Project addProject(Project project, String userEmail);
    Project getProjectById(Long id) throws Exception;
    Project updateProjectById(Project project, String userEmail)throws Exception;
    boolean deleteProjectById(Long id, String userEmail);

}
