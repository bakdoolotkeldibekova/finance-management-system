package com.example.fms.service;

import com.example.fms.dto.CounterpartyDTO;
import com.example.fms.entity.*;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.CounterpartyRepository;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.UserRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
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
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Counterparty> getAll(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedCounterpartyFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Counterparty> counterparties = counterpartyRepository.findAllByOrderByDateCreatedDesc();
        session.disableFilter("deletedCounterpartyFilter");
        return counterparties;
    }

    @Override
    public Page<Counterparty> getByPage(List<Counterparty> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<Counterparty>(list.subList(start, end), pageable, list.size());

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
    public ResponseEntity<Counterparty> addCounterparty(CounterpartyDTO counterpartyDTO, String userEmail) {
        Counterparty counterparty = new Counterparty(counterpartyDTO.getName());
        counterpartyRepository.save(counterparty);

        Journal journal = new Journal();
        journal.setTable("COUNTERPARTY: " + counterpartyDTO.getName());
        journal.setAction("create");
        journal.setUser(userRepository.findByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return ResponseEntity.ok().body(counterparty);
    }

    @Override
    public ResponseEntity<Counterparty> getCounterpartyById(Long id) {
        Counterparty counterparty = counterpartyRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Counterparty id " + id + " not found!"));
        if (counterparty.isDeleted())
            throw new ResourceNotFoundException("Counterparty id " + id + " was deleted!");
        return ResponseEntity.ok().body(counterparty);
    }

    @Override
    public ResponseEntity<Counterparty> updateCounterpartyById(CounterpartyDTO counterpartyDTO, Long id, String userEmail) {
         Counterparty result = counterpartyRepository.findById(id)
                .map(counterparty -> {
                    if (counterparty.isDeleted())
                        throw new ResourceNotFoundException("Counterparty id " + id + " was deleted!");
                    counterparty.setName(counterpartyDTO.getName());
                    return counterpartyRepository.save(counterparty);
                })
                 .orElseThrow(()->new ResourceNotFoundException("Counterparty id " + id + " not found!"));

            Journal journal = new Journal();
            journal.setTable("COUNTERPARTY: " + counterpartyDTO.getName());
            journal.setAction("update");
            journal.setUser(userRepository.findByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseMessage deleteCounterpartyById(Long id, String userEmail) {
        Counterparty counterparty = counterpartyRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Counterparty id " + id + " not found!"));

        if (counterparty.isDeleted())
            throw new ResourceNotFoundException("Counterparty id " + id + " was deleted!");

        counterpartyRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setTable("COUNTERPARTY: " + counterparty.getName());
            journal.setAction("delete");
            journal.setUser(userRepository.findByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(),"Counterparty successfully deleted");
    }
}
