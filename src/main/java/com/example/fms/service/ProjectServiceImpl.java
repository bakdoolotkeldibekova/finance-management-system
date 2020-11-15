package com.example.fms.service;

import com.example.fms.dto.ProjectDTO;
import com.example.fms.entity.Journal;
import com.example.fms.entity.Project;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    @Override
    public List<Project> getAllByNameContaining(String name) {
        return projectRepository.findAllByNameContainingIgnoringCase(name);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public List<Project> getAllByDateCreatedBefore(String before) {
        return projectRepository.findAllByDateCreatedBefore(LocalDateTime.parse(before, formatter));
    }

    @Override
    public List<Project> getAllByDateCreatedAfter(String after) {
        return projectRepository.findAllByDateCreatedAfter(LocalDateTime.parse(after, formatter));
    }

    @Override
    public Project addProject(ProjectDTO projectDTO, String userEmail) {
        Project project = new Project(projectDTO.getName());

        Journal journal = new Journal();
        journal.setTable("PROJECT: " + project.getName());
        journal.setAction("create");
        journal.setUser(userService.getByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return projectRepository.save(project);
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public Project updateProjectById(ProjectDTO projectDTO, Long id, String userEmail){
        Project result = projectRepository.findById(id)
                .map(newProject -> {
                    newProject.setName(projectDTO.getName());
                    return projectRepository.save(newProject);
                })
                .orElse(null);
        if (result != null) {
            Journal journal = new Journal();
            journal.setTable("PROJECT: " + projectDTO.getName());
            journal.setAction("update");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return result;
    }

    @Override
    public Project deleteProjectById(Long id, String userEmail) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project != null){
            projectRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setTable("PROJECT: " + project.getName());
            journal.setAction("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return project;
    }
}
