package com.example.fms.service;

import com.example.fms.dto.CounterpartyDTO;
import com.example.fms.entity.Counterparty;
import com.example.fms.entity.Journal;
import com.example.fms.repository.CounterpartyRepository;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CounterpartyServiceImpl implements CounterpartyService{
    @Autowired
    CounterpartyRepository counterpartyRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<Counterparty> getAll() {
        return counterpartyRepository.findAll();
    }

    @Override
    public List<Counterparty> getAllByNameContaining(String name) {
        return counterpartyRepository.findAllByNameContainingIgnoringCase(name);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public List<Counterparty> getAllByDateCreatedBefore(String before) {
        return counterpartyRepository.findAllByDateCreatedBefore(LocalDateTime.parse(before, formatter));
    }

    @Override
    public List<Counterparty> getAllByDateCreatedAfter(String after) {
        return counterpartyRepository.findAllByDateCreatedAfter(LocalDateTime.parse(after, formatter));
    }

    @Override
    public Counterparty addCounterparty(CounterpartyDTO counterpartyDTO, String userEmail) {
        Counterparty counterparty = new Counterparty(counterpartyDTO.getName());

        Journal journal = new Journal();
        journal.setTable("COUNTERPARTY: " + counterpartyDTO.getName());
        journal.setAction("create");
        journal.setUser(userService.getByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);
        return counterpartyRepository.save(counterparty);
    }

    @Override
    public Counterparty getCounterpartyById(Long id) {
        return counterpartyRepository.findById(id).orElse(null);
    }

    @Override
    public Counterparty updateCounterpartyById(CounterpartyDTO counterpartyDTO, Long id, String userEmail) {
         Counterparty result = counterpartyRepository.findById(id)
                .map(counterparty -> {
                    counterparty.setName(counterpartyDTO.getName());
                    return counterpartyRepository.save(counterparty);
                })
                .orElse(null);
        if (result != null) {
            Journal journal = new Journal();
            journal.setTable("COUNTERPARTY: " + counterpartyDTO.getName());
            journal.setAction("update");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }

        return result;
    }

    @Override
    public Counterparty deleteCounterpartyById(Long id, String userEmail) {
        Counterparty counterparty = counterpartyRepository.findById(id).orElse(null);
        if (counterparty != null){
            counterpartyRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setTable("COUNTERPARTY: " + counterparty.getName());
            journal.setAction("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return counterparty;
    }
}
