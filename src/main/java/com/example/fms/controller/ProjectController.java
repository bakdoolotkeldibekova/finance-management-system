package com.example.fms.controller;

import com.example.fms.entity.Project;
import com.example.fms.entity.User;
import com.example.fms.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/getAll")
    public List<Project> getAll() {
        return projectService.getAll();
    }

    @PostMapping("/add")
    public Project addProject(@RequestBody Project project, Principal principal) {
        return projectService.addProject(project, principal.getName());
    }

    @GetMapping("/{id}")
    public Project getProject(@PathVariable Long id)throws Exception {
        return projectService.getProjectById(id);
    }

    @PutMapping("/update")
    public Project updateProject(@RequestBody Project project, Principal principal) throws Exception {
        return projectService.updateProjectById(project, principal.getName());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProject(@PathVariable Long id, Principal principal) {
        projectService.deleteProjectById(id, principal.getName());
    }
}
