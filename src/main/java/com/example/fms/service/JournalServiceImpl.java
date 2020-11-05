package com.example.fms.service;

import com.example.fms.entity.Journal;
import com.example.fms.entity.User;
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

    @Override
    public List<Journal> getAll() {
        return journalRepository.findAll();
    }

    @Override
    public List<Journal> getAllByAction1(String action) {
        return journalRepository.findAllByAction1ContainingIgnoringCase(action);
    }

    @Override
    public List<Journal> getAllByAction2(String action) {
        return journalRepository.findAllByAction2ContainingIgnoringCase(action);
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
    public boolean deleteById(Long id) {
        if(journalRepository.findById(id).isPresent()){
            journalRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
