package com.example.fms.service;

import com.example.fms.dto.DepartmentDTO;
import com.example.fms.entity.Counterparty;
import com.example.fms.entity.Department;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DepartmentService {

    List<Department> getAll();
    Page<Department> getByPage(List<Department> list, Pageable pageable);
    List<Department> getAllByNameContaining(String name);
    List<Department> getAllByDateCreatedBefore(String before);
    List<Department> getAllByDateCreatedAfter(String after);
    ResponseEntity<Department> addDepartment (DepartmentDTO departmentDTO, String userEmail);

    ResponseEntity<Department> getDepartmentById(Long id);

    ResponseEntity<Department> updateDepartmentById(DepartmentDTO departmentDTO, Long id, String userEmail);

    ResponseMessage deleteDepartmentById(Long id, String userEmail);

}
