package com.example.fms.service;

import com.example.fms.dto.DepartmentDTO;
import com.example.fms.entity.Department;
import com.example.fms.entity.Journal;
import com.example.fms.repository.DepartmentRepository;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService{
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    @Override
    public List<Department> getAllByNameContaining(String name) {
        return departmentRepository.findAllByNameContainingIgnoringCase(name);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public List<Department> getAllByDateCreatedBefore(String before) {
        return departmentRepository.findAllByDateCreatedBefore(LocalDateTime.parse(before, formatter));
    }

    @Override
    public List<Department> getAllByDateCreatedAfter(String after) {
        return departmentRepository.findAllByDateCreatedAfter(LocalDateTime.parse(after, formatter));
    }

    @Override
    public Department addDepartment(DepartmentDTO departmentDTO, String userEmail) {
        Department department = new Department(departmentDTO.getName());

        Journal journal = new Journal();
        journal.setTable("DEPARTMENT: " + departmentDTO.getName());
        journal.setAction("create");
        journal.setUser(userService.getByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);
        return departmentRepository.save(department);
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    @Override
    public Department updateDepartmentById(DepartmentDTO departmentDTO, Long id, String userEmail) {
        Department result = departmentRepository.findById(id)
                .map(department -> {
                    department.setName(departmentDTO.getName());
                    return departmentRepository.save(department);
                })
                .orElse(null);
        if (result != null) {
            Journal journal = new Journal();
            journal.setTable("DEPARTMENT: " + departmentDTO.getName());
            journal.setAction("update");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return result;
    }

    @Override
    public Department deleteDepartmentById(Long id, String userEmail) {
        Department department = departmentRepository.findById(id).orElse(null);
        if (department != null){
            departmentRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setTable("DEPARTMENT: " + department.getName());
            journal.setAction("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return department;
    }
}
