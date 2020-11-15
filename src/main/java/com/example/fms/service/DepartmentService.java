package com.example.fms.service;

import com.example.fms.dto.DepartmentDTO;
import com.example.fms.entity.Counterparty;
import com.example.fms.entity.Department;
import com.example.fms.entity.User;

import java.util.List;

public interface DepartmentService {

    List<Department> getAll();
    List<Department> getAllByNameContaining(String name);
    List<Department> getAllByDateCreatedBefore(String before);
    List<Department> getAllByDateCreatedAfter(String after);
    Department addDepartment (DepartmentDTO departmentDTO, String userEmail);

    Department getDepartmentById(Long id);

    Department updateDepartmentById(DepartmentDTO departmentDTO, Long id, String userEmail);

    Department deleteDepartmentById(Long id, String userEmail);

}
