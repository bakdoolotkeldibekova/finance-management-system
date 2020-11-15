package com.example.fms.service;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.Staff;
import com.example.fms.entity.User;

import java.util.List;

public interface StaffService {
    List<Staff> getAll();
    Staff addStaff(StaffDTO newStaff, String userEmail);
    Staff getStaffById(Long id);
    List<Staff> getAllStaffByName(String name);
    String updateStaffById (StaffDTO newStaff, Long id, String userEmail);
    Staff deleteStaffById(Long id, String userEmail);

    List<Staff> getAllByDateCreatedAfter(String after);
    List<Staff> getAllByDateCreatedBefore(String before);
}
