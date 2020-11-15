package com.example.fms.controller;

import com.example.fms.dto.CounterpartyDTO;
import com.example.fms.entity.Counterparty;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import com.example.fms.service.CounterpartyService;
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
@RequestMapping("/counterparty")
public class CounterpartyController {

    CounterpartyService counterpartyService;
    private final UserService userService;

    CounterpartyController(CounterpartyService counterpartyService, UserService userService) {
        this.counterpartyService = counterpartyService;
        this.userService = userService;
    }

    @GetMapping("/get")
    public List<Counterparty> getAllByParam(HttpServletRequest request, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get counterparty information");
        }
        String name = request.getParameter("name");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");

        Set<Counterparty> fooSet = new LinkedHashSet<>(counterpartyService.getAll());

        if (name != null)
            fooSet.retainAll(counterpartyService.getAllByNameContaining(name));
        if (dateAfter != null)
            fooSet.retainAll(counterpartyService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(counterpartyService.getAllByDateCreatedBefore(dateBefore));

        return new ArrayList<>(fooSet);
    }

    @GetMapping("/{id}")
    public Counterparty getCounterparty(@PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get counterparty information");
        }
        Counterparty counterparty = counterpartyService.getCounterpartyById(id);
        if (counterparty != null) {
            return counterparty;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Counterparty id " + id + " not found!");
    }

    @PostMapping("/add")
    public ResponseMessage addCounterparty(@RequestBody CounterpartyDTO counterpartyDTO, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to add counterparty");
        }
        counterpartyService.addCounterparty(counterpartyDTO, principal.getName());
        return new ResponseMessage(HttpStatus.OK.value(), "Counterparty successfully saved");
    }

    @PutMapping("/update/{id}")
    public ResponseMessage updateCounterparty(@RequestBody CounterpartyDTO counterpartyDTO, @PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to change counterparty information");
        }
        Counterparty counterparty1 = counterpartyService.updateCounterpartyById(counterpartyDTO, id, principal.getName());
        if (counterparty1 != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Counterparty successfully updated");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Counterparty id " + id + " not found!");
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteCounterparty(@PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to delete counterparty information");
        }
        Counterparty counterparty = counterpartyService.deleteCounterpartyById(id, principal.getName());
        if (counterparty != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Counterparty successfully deleted");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Counterparty id " + id + " not found!");
    }

}
