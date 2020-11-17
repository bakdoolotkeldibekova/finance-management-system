package com.example.fms.controller;

import com.example.fms.dto.DepartmentDTO;
import com.example.fms.entity.Department;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import com.example.fms.service.DepartmentService;
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
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserService userService;

    DepartmentController(DepartmentService service, UserService userService) {
        this.departmentService = service;
        this.userService = userService;
    }

    @GetMapping("/get")
    public List<Department> getAllByParam(@RequestParam(required = false) String name,
                                          @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                          @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get department information");
        }
        Set<Department> fooSet = new LinkedHashSet<>(departmentService.getAll());

        if (name != null)
            fooSet.retainAll(departmentService.getAllByNameContaining(name));
        if (dateAfter != null)
            fooSet.retainAll(departmentService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(departmentService.getAllByDateCreatedBefore(dateBefore));

        return new ArrayList<>(fooSet);
    }

    @PostMapping("/add")
    public Department addDepartment (@RequestBody DepartmentDTO departmentDTO, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to add department");
        }
        return departmentService.addDepartment(departmentDTO, principal.getName());
    }

    @GetMapping("/{id}")
    public ResponseMessage getDepartment(@PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get department information");
        }
        Department department = departmentService.getDepartmentById(id);
        if (department != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Department successfully saved");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department id " + id + " not found!");
    }

    @PutMapping ("/update/{id}")
    public ResponseMessage updateDepartment(@RequestBody DepartmentDTO departmentDTO, @PathVariable Long id, Principal principal){
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to change department information");
        }
        Department department = departmentService.updateDepartmentById(departmentDTO, id, principal.getName());
        if (department != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Department successfully updated");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department id " + id + " not found!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteDepartment(@PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to delete department information");
        }
        Department department = departmentService.deleteDepartmentById(id, principal.getName());
        if (department != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Department successfully deleted");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department id " + id + " not found!");
    }

}
