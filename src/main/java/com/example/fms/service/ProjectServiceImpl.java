package com.example.fms.service;

import com.example.fms.dto.ProjectDTO;
import com.example.fms.entity.Journal;
import com.example.fms.entity.Project;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.ProjectRepository;
import com.example.fms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private UserRepository userRepository;

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
    public ResponseEntity<Project> addProject(ProjectDTO projectDTO, String userEmail) {
        Project project = new Project(projectDTO.getName());
        projectRepository.save(project);

        Journal journal = new Journal();
        journal.setTable("PROJECT: " + project.getName());
        journal.setAction("create");
        journal.setUser(userRepository.findByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return ResponseEntity.ok().body(project);
    }

    @Override
    public ResponseEntity<Project> getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project id " + id + " not found!"));
        return ResponseEntity.ok().body(project);
    }

    @Override
    public ResponseEntity<Project> updateProjectById(ProjectDTO projectDTO, Long id, String userEmail){
        Project result = projectRepository.findById(id)
                .map(newProject -> {
                    newProject.setName(projectDTO.getName());
                    return projectRepository.save(newProject);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Project id " + id + " not found!"));

            Journal journal = new Journal();
            journal.setTable("PROJECT: " + projectDTO.getName());
            journal.setAction("update");
            journal.setUser(userRepository.findByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseMessage deleteProjectById(Long id, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project id " + id + " not found!"));
            projectRepository.deleteById(id);

            Journal journal = new Journal();
            journal.setTable("PROJECT: " + project.getName());
            journal.setAction("delete");
            journal.setUser(userRepository.findByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(), "Project successfully deleted");
    }
}
