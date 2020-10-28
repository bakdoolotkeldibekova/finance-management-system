package com.example.fms.controller;

import com.example.fms.entity.Counterparty;
import com.example.fms.entity.User;
import com.example.fms.service.CounterpartyService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/counterparty")
public class CounterpartyController {

    CounterpartyService counterpartyService;

    CounterpartyController(CounterpartyService counterpartyService) {
        this.counterpartyService = counterpartyService;
    }

    @GetMapping("/getAll")
    public List<Counterparty> getAll() {
        return counterpartyService.getAll();
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
