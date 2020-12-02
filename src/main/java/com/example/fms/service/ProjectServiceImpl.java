package com.example.fms.service;

import com.example.fms.dto.ProjectDTO;
import com.example.fms.entity.*;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.ProjectRepository;
import com.example.fms.repository.UserRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Project> getAll(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProjectFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Project> projects = projectRepository.findAllByOrderByDateCreatedDesc();
        session.disableFilter("deletedProjectFilter");
        return projects;
    }

    @Override
    public Page<Project> getByPage(List<Project> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        List<Project> output = new ArrayList<>();
        if (start <= end) {
            output = list.subList(start, end);
        }
        return new PageImpl<>(output, pageable, list.size());

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
        if (project.isDeleted())
            throw new ResourceNotFoundException("Project id " + id + " was deleted!");
        return ResponseEntity.ok().body(project);
    }

    @Override
    public ResponseEntity<Project> updateProjectById(ProjectDTO projectDTO, Long id, String userEmail){
        Project result = projectRepository.findById(id)
                .map(newProject -> {
                    if (newProject.isDeleted())
                        throw new ResourceNotFoundException("Project id " + id + " was deleted!");
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
        if (project.isDeleted())
            throw new ResourceNotFoundException("Project id " + id + " was deleted!");

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
