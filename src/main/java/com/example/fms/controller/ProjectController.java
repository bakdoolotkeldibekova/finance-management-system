package com.example.fms.controller;

import com.example.fms.dto.ProjectDTO;
import com.example.fms.entity.Project;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import com.example.fms.service.ProjectService;
import com.example.fms.service.UserService;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/get")
    public @ResponseBody Page<Project> getAllByParam(Pageable pageable,
                                                     @RequestParam(value = "isDeleted", required = false, defaultValue = "false") boolean isDeleted,
                                                     @RequestParam(required = false) String name,
                                                     @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                                     @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore) {

        Set<Project> fooSet = new LinkedHashSet<>(projectService.getAll(isDeleted));

        if (name != null)
            fooSet.retainAll(projectService.getAllByNameContaining(name));
        if (dateAfter != null)
            fooSet.retainAll(projectService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(projectService.getAllByDateCreatedBefore(dateBefore));

        List<Project> list = new ArrayList<>(fooSet);
        return projectService.getByPage(list, pageable);
    }

    @PostMapping("/add")
    public ResponseEntity<Project> addProject(@RequestBody ProjectDTO projectDTO, Principal principal) {
        return projectService.addProject(projectDTO, principal.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getById(@PathVariable Long id){
        return projectService.getProjectById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Project> updateProject(@RequestBody ProjectDTO projectDTO, @PathVariable Long id, Principal principal){
        return projectService.updateProjectById(projectDTO, id, principal.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteProject(@PathVariable Long id, Principal principal) {
        return projectService.deleteProjectById(id, principal.getName());
    }

}
