package com.example.fms.service;

import com.example.fms.entity.*;
import com.example.fms.exception.AccessDenied;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class JournalServiceImpl implements JournalService {
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JournalService journalService;

    @Override
    public ResponseEntity<Journal> getByIdForAdmin(Long id){
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal id " + id + " not found!"));
        return ResponseEntity.ok().body(journal);
    }

    @Override
    public ResponseEntity<Journal> getByIdForUser(Long id, String email){
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal id " + id + " not found!"));
        if (journal.isDeleted())
            throw new ResourceNotFoundException("Journal id " + id + " was deleted!");

        if (journal.getUser() != null) {
            List<Department> departmentList = userRepository.findByEmail(email).getDepartments();
            departmentList.retainAll(journal.getUser().getDepartments());

            if (!departmentList.isEmpty())
                return ResponseEntity.ok().body(journal);
        }
        throw new AccessDenied("You do not have access to this journal record");
    }

    @Override
    public List<Journal> getAll(boolean isDeleted, String email) {
        User user = userRepository.findByEmail(email);
        if (isDeleted && !user.getRole().getName().equals("ROLE_ADMIN"))
            throw new AccessDenied("Deleted journal records are not available for you");
        List<Journal> journals = new ArrayList<>();
        for (Journal journal : journalRepository.findAllByDeletedOrderByDateCreatedDesc(isDeleted)) {
            List<Department> departments = user.getDepartments();
            if (journal.getUser() != null){
                departments.retainAll(journal.getUser().getDepartments());
                if (!departments.isEmpty())
                    journals.add(journal);
            }
        }
        return journals;
    }

    @Override
    public Page<Journal> getByPage(List<Journal> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        List<Journal> output = new ArrayList<>();
        if (start <= end) {
            output = list.subList(start, end);
        }
        return new PageImpl<>(output, pageable, list.size());
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
        Journal oldJournal;
        if (userRepository.findByEmail(userEmail).getRole().equals("ROLE_ADMIN"))
            oldJournal = journalService.getByIdForAdmin(id).getBody();
        else
            oldJournal = journalService.getByIdForUser(id, userEmail).getBody();

        oldJournal.setDeleted(true);
        journalRepository.save(oldJournal);

        Journal journal = new Journal();
        journal.setTable("JOURNAL");
        journal.setAction("delete");
        journal.setUser(userRepository.findByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);
        return new ResponseMessage(HttpStatus.OK.value(), "Journal id " + id +" deleted successfully");
    }
}
