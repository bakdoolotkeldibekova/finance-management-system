package com.example.fms.controller;

import com.example.fms.dto.CounterpartyDTO;
import com.example.fms.entity.Counterparty;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.service.CounterpartyService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/counterparty")
public class CounterpartyController {

    private final CounterpartyService counterpartyService;
    CounterpartyController(CounterpartyService counterpartyService) {
        this.counterpartyService = counterpartyService;
    }

    @GetMapping("/get")
    public List<Counterparty> getAllByParam(@RequestParam(required = false) String name,
                                            @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                            @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore) {

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
    public ResponseEntity<Counterparty> getById(@PathVariable Long id) {
        return counterpartyService.getCounterpartyById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Counterparty> addCounterparty(@RequestBody CounterpartyDTO counterpartyDTO, Principal principal) {
        return counterpartyService.addCounterparty(counterpartyDTO, principal.getName());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Counterparty> updateCounterparty(@RequestBody CounterpartyDTO counterpartyDTO, @PathVariable Long id, Principal principal) {
       return counterpartyService.updateCounterpartyById(counterpartyDTO, id, principal.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteCounterparty(@PathVariable Long id, Principal principal) {
        return counterpartyService.deleteCounterpartyById(id, principal.getName());
    }

}
