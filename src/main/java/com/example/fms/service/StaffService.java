package com.example.fms.service;

import com.example.fms.entity.Staff;
import com.example.fms.entity.User;

import java.util.List;

public interface StaffService {
    List<Staff> getAll();
    Staff addStaff(Staff newStaff, String userEmail);
    Staff getStaffById(Long id) throws Exception;
    List<Staff> getAllStaffByName(String name);
    Staff updateStaffById (Staff newStaff, String userEmail) throws Exception;
    boolean deleteStaffById(Long id, String userEmail);
}
