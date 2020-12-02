package com.example.fms.service;

import com.example.fms.entity.Journal;
import com.example.fms.entity.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface JournalService {
    ResponseEntity<Journal> getByIdForAdmin(Long id);
    ResponseEntity<Journal> getByIdForUser(Long id, String email);
    List<Journal> getAllForAdmin();
    List<Journal> getAllForUser(String email);
    Page<Journal> getByPage(List<Journal> list, Pageable pageable);
    List<Journal> getAllByTable(String table);
    List<Journal> getAllByAction(String action);
    List<Journal> getAllByUser(Long userId);

    List<Journal> getAllByDateCreatedAfter(String after);
    List<Journal> getAllByDateCreatedBefore(String before);

    ResponseMessage deleteById(Long id, String userEmail);
}
