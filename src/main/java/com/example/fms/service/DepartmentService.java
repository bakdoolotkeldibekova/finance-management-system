package com.example.fms.service;

import com.example.fms.entity.Department;
import com.example.fms.entity.User;

import java.util.List;

public interface DepartmentService {

    List<Department> getAll();
    Department addDepartment (Department newDepartment, String userEmail);

    Department getDepartmentById(Long id) throws Exception;

    Department updateDepartmentById(Department newDepartment, String userEmail)throws Exception;

    boolean deleteDepartmentById(Long id, String userEmail);

}
