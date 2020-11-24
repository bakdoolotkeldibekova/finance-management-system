package com.example.fms.service;

import com.example.fms.entity.Journal;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Transaction;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JournalServiceImpl implements JournalService {
    @Autowired
    private JournalRepository journalRepository;

    @Override
    public ResponseEntity<Journal> getByIdForAdmin(Long id){
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal id " + id + " not found!"));
        return ResponseEntity.ok().body(journal);
    }

    @Override
    public ResponseEntity<Journal> getByIdForUser(Long id){
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal id " + id + " not found!"));
        if (journal.isDeleted())
            throw new ResourceNotFoundException("Journal id " + id + " not found!");
        return ResponseEntity.ok().body(journal);
    }

    @Override
    public List<Journal> getAllForUser() {
        return journalRepository.findAllByDeleted(false);
    }

    @Override
    public Page<Journal> getByPage(List<Journal> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<Journal>(list.subList(start, end), pageable, list.size());

    }

    @Override
    public List<Journal> getAllForAdmin() {
        return journalRepository.findAll();
    }

    @Override
    public List<Journal> getAllByTable(String table) {
        return journalRepository.findAllByTableContainingIgnoringCase(table);
    }

    @Override
    public List<Journal> getAllByAction(String action) {
        return journalRepository.findAllByActionContainingIgnoringCase(action);
    }

    @Override
    public List<Journal> getAllByUser(Long userId) {
        return journalRepository.findAllByUserId(userId);
    }

    @Override
    public List<Journal> getAllByDateCreatedAfter(String after) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(after, formatter);
        return journalRepository.findAllByDateCreatedAfter(dateTime);
    }

    @Override
    public List<Journal> getAllByDateCreatedBefore(String before) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(before, formatter);
        return journalRepository.findAllByDateCreatedBefore(dateTime);
    }

    @Override
    public ResponseMessage deleteById(Long id, String userEmail) {
        Journal journal1 = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal id " + id + " not found!"));
        if (journal1.isDeleted())
            throw new ResourceNotFoundException("Journal id " + id + " was deleted!");

        journal1.setDeleted(true);
        journalRepository.save(journal1);

//            Journal journal = new Journal();
//            journal.setTable("JOURNAL");
//            journal.setAction("delete");
//            journal.setUser(userRepository.findByEmail(userEmail));
//            journal.setDeleted(false);
//            journalRepository.save(journal);
       return new ResponseMessage(HttpStatus.OK.value(), "Journal id " + id +" deleted successfully");
    }
}
