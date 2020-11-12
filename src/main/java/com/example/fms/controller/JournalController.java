package com.example.fms.controller;

import com.example.fms.entity.Journal;
import com.example.fms.entity.Role;
import com.example.fms.service.JournalService;
import com.example.fms.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    public boolean deleteById(@PathVariable Long id, Principal principal) throws Exception{
        return journalService.deleteById(id, principal.getName());
    }

    @GetMapping("/{id}")
    public Journal getById(@PathVariable("id") Long id, Principal principal) throws Exception{
        String email = principal.getName();
        boolean isAdmin = false;
        for (Role item : userService.getByEmail(email).getRoles()) {
            if (item.getName().equalsIgnoreCase("ADMIN")) {
                isAdmin = true; break;
            }
        }
        if (isAdmin) return journalService.getById(id);
        else {
            Journal journal = journalService.getById(id);
            if (journal.isDeleted())
                throw  new NotFoundException("Action number" + id + " has not found");
            else return journal;
        }
    }

    @GetMapping("/get")
    public List<Journal> getAllByParam(HttpServletRequest request, Principal principal){
        boolean isAdmin = false;
        for (Role item : userService.getByEmail(principal.getName()).getRoles()) {
            if (item.getName().equalsIgnoreCase("ADMIN")) {
                isAdmin = true; break;
            }
        }
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
