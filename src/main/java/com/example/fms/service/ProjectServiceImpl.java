package com.example.fms.service;

import com.example.fms.entity.Department;
import com.example.fms.entity.Journal;
import com.example.fms.entity.Project;
import com.example.fms.entity.User;
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
    public Project addProject(Project project, String userEmail) {
        Journal journal = new Journal();
        journal.setAction1("PROJECT: " + project.getName());
        journal.setAction2("create");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);

        return projectRepository.save(project);
    }

    @Override
    public Project getProjectById(Long id) throws Exception{
        return projectRepository.findById(id).orElseThrow(Exception::new);
    }

    @Override
    public Project updateProjectById(Project project, String userEmail) throws Exception{
        Project result = projectRepository.findById(project.getId())
                .map(newProject -> {
                    newProject.setName(project.getName());
                    return projectRepository.save(newProject);
                })
                .orElseThrow(Exception::new);

        Journal journal = new Journal();
        journal.setAction1("PROJECT: " + project.getName());
        journal.setAction2("update");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);

        return result;
    }

    @Override
    public boolean deleteProjectById(Long id, String userEmail) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project != null){
            projectRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setAction1("PROJECT: " + project.getName());
            journal.setAction2("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journalRepository.save(journal);
            return true;
        }
        return false;
    }
}
