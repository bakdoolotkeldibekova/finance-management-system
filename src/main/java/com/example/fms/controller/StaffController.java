package com.example.fms.controller;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Staff;
import com.example.fms.entity.User;
import com.example.fms.service.StaffService;
import com.example.fms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final UserService userService;

    StaffController(StaffService staffService, UserService userService) {
        this.staffService = staffService;
        this.userService = userService;
    }

    @GetMapping("/get")
    public List<Staff> getAllByParam(HttpServletRequest request, Principal principal){
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get staff information");
        }
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

    @PostMapping("/add")
    public ResponseMessage addStaff(@RequestBody StaffDTO newStaff, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to add staff");
        }
        Staff staff = staffService.addStaff(newStaff, principal.getName());
        if (staff != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Staff successfully updated");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department id not found!");
    }

    @GetMapping("/{id}")
    public Staff getStaff(@PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get staff information");
        }
        Staff staff = staffService.getStaffById(id);
        if (staff != null) {
            return staff;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff id " + id + " not found!");
    }

    @PutMapping("/update/{id}")
    public ResponseMessage updateStaff (@RequestBody StaffDTO staffDTO, @PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to change staff information");
        }
        String staff = staffService.updateStaffById(staffDTO, id, principal.getName());
        if (staff.equals("success"))
            return new ResponseMessage(HttpStatus.OK.value(), "Staff successfully updated");
        else if (staff.equals("not found staff"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff id " + id + " not found!");
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department id not found!");
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteStaff(@PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to delete staff information");
        }
        Staff staff = staffService.deleteStaffById(id, principal.getName());
        if (staff != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Staff successfully deleted");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff id " + id + " not found!");
    }

}



