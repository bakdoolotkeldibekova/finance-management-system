package com.example.fms.controller;

import com.example.fms.entity.Staff;
import com.example.fms.entity.User;
import com.example.fms.service.StaffService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {
    private final StaffService staffService;


    StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/getAll")
    public List<Staff> getAll() {
        return staffService.getAll();
    }

    @PostMapping("/add")
    public Staff addStaff(@RequestBody Staff newStaff, Principal principal) {
        return staffService.addStaff(newStaff, principal.getName());
    }

    @GetMapping("/{id}")
    public Object getStaff(@PathVariable Long id) throws Exception{
        return staffService.getStaffById(id);
    }

    @GetMapping("/getByName")
    public List<Staff> getByName(@RequestBody String name) {
        return staffService.getAllStaffByName(name);
    }

    @PutMapping("/update")
    public Staff updateStaff (@RequestBody Staff newStaff, Principal principal) throws Exception {
        return staffService.updateStaffById(newStaff, principal.getName());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteStaff(@PathVariable Long id, Principal principal) {
        staffService.deleteStaffById(id, principal.getName());
    }

}
