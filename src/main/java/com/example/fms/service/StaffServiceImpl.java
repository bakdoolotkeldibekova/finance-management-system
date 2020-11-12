package com.example.fms.service;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.*;
import com.example.fms.repository.DepartmentRepository;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private UserService userService;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<Staff> getAll() {
        return staffRepository.findAll();
    }

    @Override
    public Staff addStaff(StaffDTO newStaff, String userEmail) throws Exception{
        Staff staff = new Staff();
        staff.setName(newStaff.getName());
        staff.setPosition(newStaff.getPosition());
        staff.setSalary(newStaff.getSalary());
        staff.setDate(newStaff.getDate());
        staff.setSalary(newStaff.getSalary());
        List<Department> dep = new ArrayList<>();
        for (Long item : newStaff.getDepartments()) {
            dep.add(departmentRepository.findById(item).orElseThrow(Exception::new));
        }
        staff.setDepartments(dep);

        Journal journal = new Journal();
        journal.setTable("STAFF: " + newStaff.getName());
        journal.setAction("create");
        journal.setUser(userService.getByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return staffRepository.save(staff);
    }

    @Override
    public Staff getStaffById(Long id) throws Exception {
        return staffRepository.findById(id).orElseThrow(Exception::new);
    }

    @Override
    public List<Staff> getAllStaffByName(String name) {
        return staffRepository.findAllByNameContainingIgnoringCase(name);
    }

    @Override
    public Staff updateStaffById(StaffDTO newStaff, String userEmail) throws Exception {

        List<Department> dep = new ArrayList<>();
        for (Long item : newStaff.getDepartments()) {
            dep.add(departmentRepository.findById(item).orElseThrow(Exception::new));
        }
        Staff result = staffRepository.findById(newStaff.getId())
                .map(staff -> {
                    staff.setName(newStaff.getName());
                    staff.setDepartments(dep);
                    staff.setPosition(newStaff.getPosition());
                    staff.setSalary(newStaff.getSalary());
                    staff.setDate(newStaff.getDate());
                    staff.setAccepted(newStaff.getAccepted());
                    return staffRepository.save(staff);
                })
                .orElseThrow(Exception::new);

        Journal journal = new Journal();
        journal.setTable("STAFF: " + newStaff.getName());
        journal.setAction("update");
        journal.setUser(userService.getByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return result;
    }

    @Override
    public boolean deleteStaffById(Long id, String userEmail) {
        Staff staff = staffRepository.findById(id).orElse(null);
        if (staff != null){
            staffRepository.deleteById(id);

            Journal journal = new Journal();
            journal.setTable("STAFF: " + staff.getName());
            journal.setAction("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

            return true;
        }
        return false;
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
