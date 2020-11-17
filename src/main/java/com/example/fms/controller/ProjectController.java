package com.example.fms.controller;

import com.example.fms.dto.ProjectDTO;
import com.example.fms.entity.Project;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import com.example.fms.service.ProjectService;
import com.example.fms.service.UserService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final UserService userService;

    ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/get")
    public @ResponseBody List<Project> getAllByParam(@RequestParam String name,
                                                     @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                                     @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get project information");
        }
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
    public ResponseMessage addProject(@RequestBody ProjectDTO projectDTO, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to add project");
        }
        projectService.addProject(projectDTO, principal.getName());
        return new ResponseMessage(HttpStatus.OK.value(), "Project successfully saved");
    }

    @GetMapping("/{id}")
    public Project getProject(@PathVariable Long id, Principal principal){
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get project information");
        }
        Project project = projectService.getProjectById(id);
        if (project != null) {
            return project;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project id " + id + " not found!");
    }

    @PutMapping("/update/{id}")
    public ResponseMessage updateProject(@RequestBody ProjectDTO projectDTO, @PathVariable Long id, Principal principal) throws Exception {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to change project information");
        }
        Project project1 = projectService.updateProjectById(projectDTO, id, principal.getName());
        if (project1 != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Project successfully updated");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project id " + id + " not found!");
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteProject(@PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to delete project information");
        }
        Project project = projectService.deleteProjectById(id, principal.getName());
        if (project != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Project successfully deleted");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project id " + id + " not found!");
    }

}
