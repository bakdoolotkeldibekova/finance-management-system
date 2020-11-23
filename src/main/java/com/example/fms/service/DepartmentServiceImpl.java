package com.example.fms.service;

import com.example.fms.dto.DepartmentDTO;
import com.example.fms.entity.Department;
import com.example.fms.entity.Journal;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Transaction;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.DepartmentRepository;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private UserRepository userRepository;

    @Override
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    @Override
    public Page<Department> getByPage(List<Department> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<Department>(list.subList(start, end), pageable, list.size());

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
    public ResponseEntity<Department> addDepartment(DepartmentDTO departmentDTO, String userEmail) {
        Department department = new Department(departmentDTO.getName());
        departmentRepository.save(department);

        Journal journal = new Journal();
        journal.setTable("DEPARTMENT: " + departmentDTO.getName());
        journal.setAction("create");
        journal.setUser(userRepository.findByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return ResponseEntity.ok().body(department);
    }

    @Override
    public ResponseEntity<Department> getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Department id " + id + " not found!"));
        return ResponseEntity.ok().body(department);
    }

    @Override
    public ResponseEntity<Department> updateDepartmentById(DepartmentDTO departmentDTO, Long id, String userEmail) {
        Department result = departmentRepository.findById(id)
                .map(department -> {
                    department.setName(departmentDTO.getName());
                    return departmentRepository.save(department);
                })
                .orElseThrow(()->new ResourceNotFoundException("Department id " + id + " not found!"));

            Journal journal = new Journal();
            journal.setTable("DEPARTMENT: " + departmentDTO.getName());
            journal.setAction("update");
            journal.setUser(userRepository.findByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseMessage deleteDepartmentById(Long id, String userEmail) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Department id " + id + " not found!"));
            departmentRepository.deleteById(id);

            Journal journal = new Journal();
            journal.setTable("DEPARTMENT: " + department.getName());
            journal.setAction("delete");
            journal.setUser(userRepository.findByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(), "Department successfully deleted");
    }
}
