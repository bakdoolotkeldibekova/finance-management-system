package com.example.fms.service;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.*;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.DepartmentRepository;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.StaffRepository;
import com.example.fms.repository.UserRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StaffServiceImpl implements StaffService{
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Staff> getAll(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedStaffFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Staff> staffList = staffRepository.findAllByOrderByDateCreatedDesc();
        session.disableFilter("deletedStaffFilter");
        return staffList;
    }

    @Override
    public Page<Staff> getByPage(List<Staff> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<Staff>(list.subList(start, end), pageable, list.size());
    }

    @Override
    public ResponseEntity<Staff> addStaff(StaffDTO newStaff, String userEmail) {
        List<Department> departmentList = new ArrayList<>();
        for (Long id : newStaff.getDepartments()) {
            Department department = departmentService.getDepartmentById(id).getBody();
            departmentList.add(department);
        }
        Staff staff = new Staff(newStaff.getName(),
                departmentList,
                newStaff.getPosition(),
                newStaff.getSalary(),
                newStaff.getDate(),
                newStaff.getAccepted());
        staffRepository.save(staff);

            Journal journal = new Journal();
            journal.setTable("STAFF: " + newStaff.getName());
            journal.setAction("create");
            journal.setUser(userRepository.findByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

           return ResponseEntity.ok().body(staff);
    }

    @Override
    public ResponseEntity<Staff> getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff id " + id + " not found!"));
        if (staff.isDeleted())
            throw new ResourceNotFoundException("Staff id " + id + " was deleted!");
        return ResponseEntity.ok().body(staff);
    }

    @Override
    public List<Staff> getAllStaffByName(String name) {
        return staffRepository.findAllByNameContainingIgnoringCase(name);
    }

    @Override
    public ResponseEntity<Staff> updateStaffById(StaffDTO newStaff, Long id, String userEmail) {
        List<Department> departmentList = new ArrayList<>();
        for (Long depId : newStaff.getDepartments()) {
            Department department = departmentService.getDepartmentById(depId).getBody();
            departmentList.add(department);
        }

        Staff result = staffRepository.findById(id)
                .map(staff -> {
                    if (staff.isDeleted())
                        throw new ResourceNotFoundException("Staff id " + id + " was deleted!");
                    staff.setName(newStaff.getName());
                    staff.setDepartments(departmentList);
                    staff.setPosition(newStaff.getPosition());
                    staff.setSalary(newStaff.getSalary());
                    staff.setDate(newStaff.getDate());
                    staff.setAccepted(newStaff.getAccepted());
                    return staffRepository.save(staff);
                }).orElseThrow(() -> new ResourceNotFoundException("Staff id " + id + " not found!"));

        Journal journal = new Journal();
        journal.setTable("STAFF: " + newStaff.getName());
        journal.setAction("update");
        journal.setUser(userRepository.findByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseMessage deleteStaffById(Long id, String userEmail) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff id " + id + " not found!"));
        if (staff.isDeleted())
            throw new ResourceNotFoundException("Staff id " + id + " was deleted!");

        staffRepository.deleteById(id);
        Journal journal = new Journal();
        journal.setTable("STAFF: " + staff.getName());
        journal.setAction("delete");
        journal.setUser(userRepository.findByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(), "Staff successfully deleted");
    }

    @Override
    public List<Staff> getAllByDateCreatedAfter(String after) {
        //  String str = "2016-03-04 11:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(after, formatter);
        return staffRepository.findAllByDateCreatedAfter(dateTime);
    }

    @Override
    public List<Staff> getAllByDateCreatedBefore(String before) {
        //  String str = "2016-03-04 11:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(before, formatter);
        return staffRepository.findAllByDateCreatedBefore(dateTime);
    }
}
