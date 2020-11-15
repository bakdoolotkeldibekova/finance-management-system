package com.example.fms.service;

import com.example.fms.entity.Journal;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JournalServiceImpl implements JournalService {
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserService userService;

    @Override
    public Journal getById(Long id){
        return journalRepository.findById(id).orElse(null);
    }

    @Override
    public List<Journal> getAllForUser() {
        return journalRepository.findAllByDeleted(false);
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
    public Journal deleteById(Long id, String userEmail) {
        Journal journal1 = journalRepository.findById(id).orElse(null);
        if (journal1 != null) {
            journal1.setDeleted(true);

            Journal journal = new Journal();
            journal.setTable("JOURNAL");
            journal.setAction("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return journal1;
    }
}
