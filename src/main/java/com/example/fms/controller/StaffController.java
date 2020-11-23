package com.example.fms.controller;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Staff;
import com.example.fms.entity.Transaction;
import com.example.fms.service.StaffService;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get")
    public Page<Staff> getAllByParam(Pageable pageable,
                                     @RequestParam(value = "isDeleted", required = false, defaultValue = "false") boolean isDeleted,
                                     @RequestParam(required = false) String name,
                                     @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                     @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore){

       Set<Staff> fooSet = new LinkedHashSet<>(staffService.getAll(isDeleted));

        if (name != null)
            fooSet.retainAll(staffService.getAllStaffByName(name));
        if (dateAfter != null)
            fooSet.retainAll(staffService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(staffService.getAllByDateCreatedBefore(dateBefore));

        List<Staff> list = new ArrayList<>(fooSet);
        return staffService.getByPage(list, pageable);

    }

    @PostMapping("/add")
    public ResponseEntity<Staff> addStaff(@RequestBody StaffDTO newStaff, Principal principal) {
        return staffService.addStaff(newStaff, principal.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Staff> getById(@PathVariable Long id) {
        return staffService.getStaffById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Staff> updateStaff (@RequestBody StaffDTO staffDTO, @PathVariable Long id, Principal principal) {
        return staffService.updateStaffById(staffDTO, id, principal.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteStaff(@PathVariable Long id, Principal principal) {
        return staffService.deleteStaffById(id, principal.getName());
    }
}



