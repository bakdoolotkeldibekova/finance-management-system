package com.example.fms.controller;

import com.example.fms.dto.ProjectDTO;
import com.example.fms.entity.Project;
import com.example.fms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private ProjectService service;

    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name", defaultValue = "World") String name) {
        return String.format("Hello, %s", name);
    }

    @PostMapping("/add")
    public Project addProject(@RequestBody ProjectDTO project, Principal principal) {
        ResponseEntity<Project> proj = service.addProject(project, principal.getName());
        return proj.getBody();
    }

    @PostMapping("/get")
    public List<Project> getProjects(Principal principal) {
        List<Project> projects = service.getAll(false);
        return projects;
    }
}