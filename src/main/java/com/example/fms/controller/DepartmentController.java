package com.example.fms.controller;

import com.example.fms.entity.Category;
import com.example.fms.entity.Department;
import com.example.fms.entity.User;
import com.example.fms.service.DepartmentService;
import org.springframework.web.bind.annotation.*;

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

    DepartmentController(DepartmentService service) {
        this.departmentService = service;
    }

    @GetMapping("/get")
    public List<Department> getAllByParam(HttpServletRequest request) {
        String name = request.getParameter("name");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");

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
    public Department addDepartment (@RequestBody Department newDepartment, Principal principal) {
        return departmentService.addDepartment(newDepartment, principal.getName());
    }

    @GetMapping("/{id}")
    public Department getDepartment(@PathVariable Long id) throws Exception {
        return departmentService.getDepartmentById(id);
    }

    @PutMapping ("/update")
    public Department updateDepartment(@RequestBody Department newDepartment, Principal principal) throws Exception{
        return departmentService.updateDepartmentById(newDepartment, principal.getName());
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteDepartment(@PathVariable Long id, Principal principal) {
        return departmentService.deleteDepartmentById(id, principal.getName());
    }

}
