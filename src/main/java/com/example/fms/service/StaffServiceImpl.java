package com.example.fms.service;

import com.example.fms.entity.Journal;
import com.example.fms.entity.Staff;
import com.example.fms.entity.User;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffServiceImpl implements StaffService{
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<Staff> getAll() {
        return staffRepository.findAll();
    }

    @Override
    public Staff addStaff(Staff newStaff, String userEmail) {
        Journal journal = new Journal();
        journal.setAction1("STAFF");
        journal.setAction2("create");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);

        return staffRepository.save(newStaff);
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
    public Staff updateStaffById(Staff newStaff, String userEmail) throws Exception {
        Staff result = staffRepository.findById(newStaff.getId())
                .map(staff -> {
                    staff.setName(newStaff.getName());
                    staff.setDepartments(newStaff.getDepartments());
                    staff.setPosition(newStaff.getPosition());
                    staff.setSalary(newStaff.getSalary());
                    staff.setDate(newStaff.getDate());
                    staff.setAccepted(newStaff.getAccepted());
                    return staffRepository.save(staff);
                })
                .orElseThrow(Exception::new);

        Journal journal = new Journal();
        journal.setAction1("STAFF");
        journal.setAction2("update");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);

        return result;
    }

    @Override
    public boolean deleteStaffById(Long id, String userEmail) {
        if (staffRepository.findById(id).isPresent()){
            Journal journal = new Journal();
            journal.setAction1("STAFF");
            journal.setAction2("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journalRepository.save(journal);

            staffRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
