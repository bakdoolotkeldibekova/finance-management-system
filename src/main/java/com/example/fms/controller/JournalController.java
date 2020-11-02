package com.example.fms.controller;

import com.example.fms.entity.Journal;
import com.example.fms.service.JournalService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@RestController
@RequestMapping("/journal")
public class JournalController {
    @Autowired
    private JournalService journalService;

    @DeleteMapping("/{id}")
    public boolean deleteById(@PathVariable Long id){
        return journalService.deleteById(id);
    }

    @GetMapping("/getAll")
    public List<Journal> getAll() {
        return journalService.getAll();
    }

    @GetMapping("/getByAction1/{action}")
    public List<Journal> getAllByAction1(@PathVariable String action) {
        return journalService.getAllByAction1(action);
    }

    @GetMapping("/getByAction2/{action}")
    public List<Journal> getAllByAction2(@PathVariable String action) {
        return journalService.getAllByAction2(action);
    }

    @GetMapping("/getByUserId/{userId}")
    public List<Journal> getAllByUserId(@PathVariable("userId") Long userId) {
        return journalService.getAllByUser(userId);
    }
}
