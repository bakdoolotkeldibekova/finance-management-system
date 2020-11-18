package com.example.fms.service;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Staff;
import com.example.fms.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StaffService {
    List<Staff> getAll();
    ResponseEntity<Staff> addStaff(StaffDTO newStaff, String userEmail);
    ResponseEntity<Staff> getStaffById(Long id);
    List<Staff> getAllStaffByName(String name);
    ResponseEntity<Staff> updateStaffById (StaffDTO newStaff, Long id, String userEmail);
    ResponseMessage deleteStaffById(Long id, String userEmail);

    List<Staff> getAllByDateCreatedAfter(String after);
    List<Staff> getAllByDateCreatedBefore(String before);
}
