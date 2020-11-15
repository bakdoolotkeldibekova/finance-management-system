package com.example.fms.controller;

import com.example.fms.entity.Journal;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Role;
import com.example.fms.service.JournalService;
import com.example.fms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalController {
    private final JournalService journalService;
    private final UserService userService;

    @Autowired
    public JournalController(JournalService journalService, UserService userService) {
        this.journalService = journalService;
        this.userService = userService;
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteById(@PathVariable Long id, Principal principal) {
        Journal journal = journalService.deleteById(id, principal.getName());
        if (journal == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal id " + id + " not found!");
        return new ResponseMessage(HttpStatus.OK.value(), "Journal id " + id +" deleted successfully");
    }

    @GetMapping("/{id}")
    public Journal getById(@PathVariable("id") Long id, Principal principal) {
        String email = principal.getName();
        boolean isAdmin = false;
        if (userService.getByEmail(email).getRole().getName().equals("ROLE_ADMIN"))
            isAdmin = true;

        if (isAdmin) return journalService.getById(id);
        else {
            Journal journal = journalService.getById(id);
            if (journal.isDeleted())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal id " + id + " not found!");
            else return journal;
        }

    }

    @GetMapping("/get")
    public List<Journal> getAllByParam(HttpServletRequest request, Principal principal){
        String email = principal.getName();
        boolean isAdmin = false;
        if (userService.getByEmail(email).getRole().getName().equals("ROLE_ADMIN"))
            isAdmin = true;

        String table = request.getParameter("table");
        String action = request.getParameter("action");
        String user = request.getParameter("user");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");

        Set<Journal> fooSet;
        if (isAdmin)
            fooSet = new LinkedHashSet<>(journalService.getAllForAdmin());
        else
            fooSet = new LinkedHashSet<>(journalService.getAllForUser());

        if (table != null)
            fooSet.retainAll(journalService.getAllByTable(table));
        if (action != null)
            fooSet.retainAll(journalService.getAllByAction(action));
        if (user != null)
            fooSet.retainAll(journalService.getAllByUser(Long.parseLong(user)));
        if (dateAfter != null)
            fooSet.retainAll(journalService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(journalService.getAllByDateCreatedBefore(dateBefore));

        return new ArrayList<>(fooSet);
    }

}
