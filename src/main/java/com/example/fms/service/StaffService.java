package com.example.fms.service;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.Staff;
import com.example.fms.entity.User;

import java.util.List;

public interface StaffService {
    List<Staff> getAll();
    Staff addStaff(StaffDTO newStaff, String userEmail) throws Exception;
    Staff getStaffById(Long id) throws Exception;
    List<Staff> getAllStaffByName(String name);
    Staff updateStaffById (StaffDTO newStaff, String userEmail) throws Exception;
    boolean deleteStaffById(Long id, String userEmail);

    List<Staff> getAllByDateCreatedAfter(String after);
    List<Staff> getAllByDateCreatedBefore(String before);
}
