package com.example.fms.service;

import com.example.fms.entity.Journal;
import com.example.fms.entity.User;

import java.util.List;

public interface JournalService {
    List<Journal> getAll();
    List<Journal> getAllByAction1(String action);
    List<Journal> getAllByAction2(String action);
    List<Journal> getAllByUser(Long userId);

    List<Journal> getAllByDateCreatedAfter(String after);
    List<Journal> getAllByDateCreatedBefore(String before);

    boolean deleteById(Long id);
}
