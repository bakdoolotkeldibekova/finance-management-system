package com.example.fms.service;

import com.example.fms.entity.Journal;

import java.util.List;

public interface JournalService {
    Journal getById(Long id);
    List<Journal> getAllForAdmin();
    List<Journal> getAllForUser();
    List<Journal> getAllByTable(String table);
    List<Journal> getAllByAction(String action);
    List<Journal> getAllByUser(Long userId);

    List<Journal> getAllByDateCreatedAfter(String after);
    List<Journal> getAllByDateCreatedBefore(String before);

    Journal deleteById(Long id, String userEmail);
}
