package com.example.fms.controller;

import com.example.fms.entity.Category;
import com.example.fms.entity.Counterparty;
import com.example.fms.entity.User;
import com.example.fms.service.CounterpartyService;
import org.springframework.web.bind.annotation.*;

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

    CounterpartyController(CounterpartyService counterpartyService) {
        this.counterpartyService = counterpartyService;
    }

    @GetMapping("/get")
    public List<Counterparty> getAllByParam(HttpServletRequest request) {
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

    @PostMapping("/add")
    public Counterparty addCounterparty(@RequestBody Counterparty counterparty, Principal principal) {
        return counterpartyService.addCounterparty(counterparty, principal.getName());
    }

    @GetMapping("/{id}")
    public Object getCounterparty(@PathVariable Long id) throws Exception {
        return counterpartyService.getCounterpartyById(id);
    }

    @PutMapping("/update")
    public Counterparty updateCounterparty(@RequestBody Counterparty counterparty, Principal principal) throws Exception{
        return counterpartyService.updateCounterpartyById(counterparty, principal.getName());
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteCounterparty(@PathVariable Long id, Principal principal) {
        return counterpartyService.deleteCounterpartyById(id, principal.getName());
    }
}
