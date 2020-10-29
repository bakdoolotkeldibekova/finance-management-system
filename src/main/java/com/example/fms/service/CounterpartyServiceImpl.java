package com.example.fms.service;

import com.example.fms.entity.Counterparty;
import com.example.fms.entity.Journal;
import com.example.fms.entity.User;
import com.example.fms.repository.CounterpartyRepository;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Counterparty addCounterparty(Counterparty newCounterparty, String userEmail) {
        Journal journal = new Journal();
        journal.setAction1("COUNTERPARTY: " + newCounterparty.getName());
        journal.setAction2("create");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);
        return counterpartyRepository.save(newCounterparty);
    }

    @Override
    public Counterparty getCounterpartyById(Long id) throws Exception {
        return counterpartyRepository.findById(id)
                .orElseThrow(Exception::new);
    }

    @Override
    public Counterparty updateCounterpartyById(Counterparty newCounterparty, String userEmail) throws Exception{
         Counterparty result = counterpartyRepository.findById(newCounterparty.getId())
                .map(counterparty -> {
                    counterparty.setName(newCounterparty.getName());
                    return counterpartyRepository.save(counterparty);
                })
                .orElseThrow(Exception::new);

        Journal journal = new Journal();
        journal.setAction1("COUNTERPARTY: " + newCounterparty.getName());
        journal.setAction2("update");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);

        return result;
    }

    @Override
    public boolean deleteCounterpartyById(Long id, String userEmail) {
        Counterparty counterparty = counterpartyRepository.findById(id).orElse(null);
        if (counterparty != null){
            counterpartyRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setAction1("COUNTERPARTY: " + counterparty.getName());
            journal.setAction2("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journalRepository.save(journal);

            return true;
        }
        return false;
    }
}
