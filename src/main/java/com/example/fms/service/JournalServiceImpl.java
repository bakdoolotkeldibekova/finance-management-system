package com.example.fms.service;

import com.example.fms.entity.Journal;
import com.example.fms.entity.User;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
