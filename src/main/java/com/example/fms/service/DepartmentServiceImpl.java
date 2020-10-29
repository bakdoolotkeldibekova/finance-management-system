package com.example.fms.service;

import com.example.fms.entity.Department;
import com.example.fms.entity.Journal;
import com.example.fms.entity.User;
import com.example.fms.repository.DepartmentRepository;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Department addDepartment(Department newDepartment, String userEmail) {
        Journal journal = new Journal();
        journal.setAction1("DEPARTMENT: " + newDepartment.getName());
        journal.setAction2("create");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);

        return departmentRepository.save(newDepartment);
    }

    @Override
    public Department getDepartmentById(Long id)throws Exception {
        return departmentRepository.findById(id).orElseThrow(Exception::new);
    }

    @Override
    public Department updateDepartmentById(Department newDepartment, String userEmail)throws Exception {
        Department result = departmentRepository.findById(newDepartment.getId())
                .map(department -> {
                    department.setName(newDepartment.getName());
                    return departmentRepository.save(department);
                })
                .orElseThrow(Exception::new);

        Journal journal = new Journal();
        journal.setAction1("DEPARTMENT: " + newDepartment.getName());
        journal.setAction2("update");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);

        return result;
    }

    @Override
    public boolean deleteDepartmentById(Long id, String userEmail) {
        Department department = departmentRepository.findById(id).orElse(null);
        if (department != null){
            Journal journal = new Journal();
            journal.setAction1("DEPARTMENT: " + department.getName());
            journal.setAction2("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journalRepository.save(journal);

            departmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
