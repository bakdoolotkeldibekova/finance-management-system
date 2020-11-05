package com.example.fms.controller;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.Staff;
import com.example.fms.service.StaffService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/staff")
public class StaffController {
    private final StaffService staffService;

    StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping("/add")
    public Staff addStaff(@RequestBody StaffDTO newStaff, Principal principal) throws Exception{
        return staffService.addStaff(newStaff, principal.getName());
    }

    @PutMapping("/update")
    public Staff updateStaff (@RequestBody StaffDTO newStaff, Principal principal) throws Exception {
        return staffService.updateStaffById(newStaff, principal.getName());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteStaff(@PathVariable Long id, Principal principal) {
        staffService.deleteStaffById(id, principal.getName());
    }

    @GetMapping("/{id}")
    public Staff getStaff(@PathVariable Long id) throws Exception{
        return staffService.getStaffById(id);
    }

    @GetMapping("/get")
    public List<Staff> getAllByParam(HttpServletRequest request){
        String name = request.getParameter("name");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");

        Set<Staff> fooSet = new LinkedHashSet<>(staffService.getAll());

        if (name != null)
            fooSet.retainAll(staffService.getAllStaffByName(name));
        if (dateAfter != null)
            fooSet.retainAll(staffService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(staffService.getAllByDateCreatedBefore(dateBefore));

        return new ArrayList<>(fooSet);
    }


}
