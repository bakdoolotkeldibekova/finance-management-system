package com.example.fms.service;

import com.example.fms.entity.Journal;
import com.example.fms.entity.ResponseMessage;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface JournalService {
    ResponseEntity<Journal> getByIdForAdmin(Long id);
    ResponseEntity<Journal> getByIdForUser(Long id);
    List<Journal> getAllForAdmin();
    List<Journal> getAllForUser();
    List<Journal> getAllByTable(String table);
    List<Journal> getAllByAction(String action);
    List<Journal> getAllByUser(Long userId);

    List<Journal> getAllByDateCreatedAfter(String after);
    List<Journal> getAllByDateCreatedBefore(String before);

    ResponseMessage deleteById(Long id, String userEmail);
}
