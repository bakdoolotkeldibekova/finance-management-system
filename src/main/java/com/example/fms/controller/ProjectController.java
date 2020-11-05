package com.example.fms.controller;

import com.example.fms.entity.Category;
import com.example.fms.entity.Project;
import com.example.fms.entity.User;
import com.example.fms.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/get")
    public List<Project> getAllByParam(HttpServletRequest request) {
        String name = request.getParameter("name");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");

        Set<Project> fooSet = new LinkedHashSet<>(projectService.getAll());

        if (name != null)
            fooSet.retainAll(projectService.getAllByNameContaining(name));
        if (dateAfter != null)
            fooSet.retainAll(projectService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(projectService.getAllByDateCreatedBefore(dateBefore));

        return new ArrayList<>(fooSet);
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
