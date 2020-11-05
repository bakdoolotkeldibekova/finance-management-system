package com.example.fms.controller;

import com.example.fms.entity.Journal;
import com.example.fms.entity.User;
import com.example.fms.service.JournalService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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


    @GetMapping("/get")
    public List<Journal> getAllByParam(HttpServletRequest request){
        String action1 = request.getParameter("action1");
        String action2 = request.getParameter("action2");
        String userId = request.getParameter("userId");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");

        Set<Journal> fooSet = new LinkedHashSet<>(journalService.getAll());

        if (action1 != null)
            fooSet.retainAll(journalService.getAllByAction1(action1));
        if (action2 != null)
            fooSet.retainAll(journalService.getAllByAction2(action2));
        if (userId != null)
            fooSet.retainAll(journalService.getAllByUser(Long.parseLong(userId)));
        if (dateAfter != null)
            fooSet.retainAll(journalService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(journalService.getAllByDateCreatedBefore(dateBefore));

        return new ArrayList<>(fooSet);
    }

}
