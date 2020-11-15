package com.example.fms.service;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.Department;
import com.example.fms.entity.Journal;
import com.example.fms.entity.Staff;
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
    public Staff addStaff(StaffDTO newStaff, String userEmail) {
        boolean check = false;
        List<Department> dep = new ArrayList<>();
        for (Long item : newStaff.getDepartments()) {
            Department department = departmentRepository.findById(item).orElse(null);
            if (department == null){
                check = true;
                break;
            }
            dep.add(department);
        }
        if (!check){
            Staff staff = new Staff();
            staff.setDepartments(dep);
            staff.setName(newStaff.getName());
            staff.setPosition(newStaff.getPosition());
            staff.setSalary(newStaff.getSalary());
            staff.setDate(newStaff.getDate());
            staff.setSalary(newStaff.getSalary());

            Journal journal = new Journal();
            journal.setTable("STAFF: " + newStaff.getName());
            journal.setAction("create");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

            return staffRepository.save(staff);
        }
        return null;
    }

    @Override
    public Staff getStaffById(Long id) {
        return staffRepository.findById(id).orElse(null);
    }

    @Override
    public List<Staff> getAllStaffByName(String name) {
        return staffRepository.findAllByNameContainingIgnoringCase(name);
    }

    @Override
    public String updateStaffById(StaffDTO newStaff, Long id, String userEmail) {
        boolean check = false;
        List<Department> dep = new ArrayList<>();
        for (Long item : newStaff.getDepartments()) {
            Department department = departmentRepository.findById(item).orElse(null);
            if (department == null){
                check = true;
                break;
            }
            dep.add(department);
        }
        if (!check){
            Staff result = staffRepository.findById(id)
                    .map(staff -> {
                        staff.setName(newStaff.getName());
                        staff.setDepartments(dep);
                        staff.setPosition(newStaff.getPosition());
                        staff.setSalary(newStaff.getSalary());
                        staff.setDate(newStaff.getDate());
                        staff.setAccepted(newStaff.getAccepted());
                        return staffRepository.save(staff);
                    })
                    .orElse(null);
            if (result != null) {
                Journal journal = new Journal();
                journal.setTable("STAFF: " + newStaff.getName());
                journal.setAction("update");
                journal.setUser(userService.getByEmail(userEmail));
                journal.setDeleted(false);
                journalRepository.save(journal);
                return "success";
            }
            return "not found staff";
        }
        return "not found dep";
    }

    @Override
    public Staff deleteStaffById(Long id, String userEmail) {
        Staff staff = staffRepository.findById(id).orElse(null);
        if (staff != null){
            staffRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setTable("STAFF: " + staff.getName());
            journal.setAction("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return staff;
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
