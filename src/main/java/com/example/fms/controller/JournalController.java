package com.example.fms.controller;

import com.example.fms.entity.Journal;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.repository.UserRepository;
import com.example.fms.service.JournalService;
import com.example.fms.service.UserService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/journal")
public class JournalController {
    private final JournalService journalService;
    private final UserRepository userRepository;

    @Autowired
    public JournalController(JournalService journalService, UserRepository userRepository) {
        this.journalService = journalService;
        this.userRepository = userRepository;
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteById(@PathVariable Long id, Principal principal) {
        return journalService.deleteById(id, principal.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Journal> getById(@PathVariable("id") Long id, Principal principal) {
        String email = principal.getName();
        if (userRepository.findByEmail(email).getRole().getName().equals("ROLE_ADMIN"))
            return journalService.getByIdForAdmin(id);
        return journalService.getByIdForUser(id);
    }

    @GetMapping("/get")
    public Page<Journal> getAllByParam(Pageable pageable,
                                       @RequestParam(required = false) String table,
                                       @RequestParam(required = false) String action,
                                       @RequestParam(required = false) Long userId,
                                       @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                       @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore, Principal principal){
        Set<Journal> fooSet;
        String email = principal.getName();
        if (userRepository.findByEmail(email).getRole().getName().equals("ROLE_ADMIN"))
            fooSet = new LinkedHashSet<>(journalService.getAllForAdmin());
        else
            fooSet = new LinkedHashSet<>(journalService.getAllForUser());

        if (table != null)
            fooSet.retainAll(journalService.getAllByTable(table));
        if (action != null)
            fooSet.retainAll(journalService.getAllByAction(action));
        if (userId != null)
            fooSet.retainAll(journalService.getAllByUser(userId));
        if (dateAfter != null)
            fooSet.retainAll(journalService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(journalService.getAllByDateCreatedBefore(dateBefore));

        List<Journal> list = new ArrayList<>(fooSet);
        return journalService.getByPage(list, pageable);
    }

}
