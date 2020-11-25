package com.example.fms.controller;

import com.example.fms.dto.DepartmentDTO;
import com.example.fms.entity.Department;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.service.DepartmentService;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/department")
public class DepartmentController {
    private final DepartmentService departmentService;
    DepartmentController(DepartmentService service) {
        this.departmentService = service;
    }

    @GetMapping("/get")
    public Page<Department> getAllByParam(Pageable pageable,
                                          @RequestParam(value = "isDeleted", required = false, defaultValue = "false") boolean isDeleted,
                                          @RequestParam(required = false) String name,
                                          @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                          @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore) {

        Set<Department> fooSet = new LinkedHashSet<>(departmentService.getAll(isDeleted));

        if (name != null)
            fooSet.retainAll(departmentService.getAllByNameContaining(name));
        if (dateAfter != null)
            fooSet.retainAll(departmentService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(departmentService.getAllByDateCreatedBefore(dateBefore));

        List<Department> list = new ArrayList<>(fooSet);
        return departmentService.getByPage(list, pageable);
    }

    @PostMapping("/add")
    public ResponseEntity<Department> addDepartment (@RequestBody DepartmentDTO departmentDTO, Principal principal) {
        return departmentService.addDepartment(departmentDTO, principal.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartment(@PathVariable Long id) {
       return departmentService.getDepartmentById(id);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<Department> updateDepartment(@RequestBody DepartmentDTO departmentDTO, @PathVariable Long id, Principal principal){
       return departmentService.updateDepartmentById(departmentDTO, id, principal.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteDepartment(@PathVariable Long id, Principal principal) {
      return departmentService.deleteDepartmentById(id, principal.getName());
    }

}
